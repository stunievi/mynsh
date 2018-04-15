package bin.leblanc.taliyah.engine.bitcask;

import bin.leblanc.taliyah.util.Serializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.zip.CRC32;

@Slf4j
public class BitcaskDataFile {

    protected Bitcask context;
    @Getter
    protected long fileId;
    protected CRC32 crc32 = new CRC32();

    @Getter
    protected int fileSize = 0;

    protected RandomAccessFile raf;
    protected FileChannel channel;

    @Getter
    protected boolean active = false;

    @Getter
    protected String filePath;

    protected FileLock lock = null;

    public BitcaskDataFile(Bitcask context, boolean active) {
        this(context,active,System.currentTimeMillis());
    }

    public BitcaskDataFile(Bitcask context, boolean active, long fileId) {
        this.context = context;
        this.fileId = fileId;
        this.setActive(active);
        init();
    }

    protected void init() {
        context.getDataFiles().put(fileId,this);

        filePath = context.getOptions().getDirPath() + File.separator + fileId + context.getOptions().getDataExtension();
        try {
            raf = new RandomAccessFile(filePath, "rw");
            channel = raf.getChannel();
            fileSize = (int) raf.length();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Object read(BitcaskIndex index) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(index.getValueSize());
        boolean hasException = false;
        Object result = null;
        try {
            lock = channel.lock();
            raf.seek(index.getValuePos());
            channel.read(byteBuffer);
            result = Serializer.unserialize(byteBuffer.array());
        } catch (IOException e) {
            e.printStackTrace();
            hasException = true;
        }
        finally {
            releaseLock();
        }
        if(result == null || hasException) return null;
        return ((BitcaskValue) result).getObj();
    }

    public BitcaskIndex write(final byte[] key, final byte[] value) {
        //只有活动文件可写
        if (!active) {
            return null;
        }
        int valuePos = 0;
        long timestamp = System.currentTimeMillis();
        byte[] timestampByte = BitConverter.getBytes(timestamp);
        byte[] keySizeByte = BitConverter.getBytes(key.length);
        byte[] valueSizeByte = BitConverter.getBytes(value == null ? 0 : value.length);

        crc32.reset();
        crc32.update(timestampByte);

        crc32.update(keySizeByte);
        crc32.update(valueSizeByte);
        crc32.update(key);
        if (value != null) {
            crc32.update(value);
        }

        boolean hasException = false;

        try{
            lock = channel.lock();
            raf.seek(raf.length());

            //crc
            channel.write(ByteBuffer.wrap(BitConverter.getBytes(crc32.getValue())));
            fileSize += Long.BYTES;

            //interval
            channel.write(ByteBuffer.wrap(timestampByte));
            fileSize += timestampByte.length;

            //keysize
            channel.write(ByteBuffer.wrap((keySizeByte)));
            fileSize += keySizeByte.length;

            //valuesize
            if (value == null || value.length == 0) {
                channel.write(ByteBuffer.wrap(BitConverter.getBytes(0)));
            } else {
                channel.write(ByteBuffer.wrap(valueSizeByte));
            }
            fileSize += Integer.BYTES;

            //key
            channel.write(ByteBuffer.wrap(key));
            fileSize += key.length;

            valuePos = fileSize;

            //value
            if (value != null && value.length > 0) {
                channel.write(ByteBuffer.wrap(value));
                fileSize += value.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
            hasException = true;
        } finally {
            releaseLock();
        }
        return hasException ? null : new BitcaskIndex(
                timestamp,
                fileId,
                value == null ? 0 : value.length,
                valuePos
                ,key);
    }

    protected void releaseLock(){
        if(lock != null){
            try {
                lock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
            lock = null;
        }
    }

    protected void setActive(boolean active){
        this.active = active;
        if(true == active){
            context.setActiveDataFileId(fileId);
        }
    }

    public void close() throws Exception {
        log.info("close");
        releaseLock();
        raf.close();
        channel.close();
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
    }

}
