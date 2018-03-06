package bin.leblanc.taliyah.engine.bitcask;

import bin.leblanc.taliyah.util.Serializer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.zip.CRC32;

@Slf4j
public class BitcaskDataFile {

    private Bitcask context;
    @Getter
    private long fileId;
    private FileOutputStream os;
    private CRC32 crc32 = new CRC32();

    @Getter
    private int fileSize = 0;

    private RandomAccessFile raf;
    private FileChannel channel;

    @Setter
    @Getter
    private boolean active = false;

    @Getter
    private String filePath;

    private FileLock lock = null;

    public BitcaskDataFile(Bitcask context, boolean active) {
        this(context,active,System.currentTimeMillis());
    }

    public BitcaskDataFile(Bitcask context, boolean active, long fileId) {
        this.context = context;
        this.active = active;
        this.fileId = fileId;
        init();
    }

    private void init() {
        os = null;
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
//        os.getFD().sync();
        ByteBuffer byteBuffer = ByteBuffer.allocate(index.getSize());
        boolean hasException = false;
        Object result = null;
        try {
            lock = channel.lock();
            raf.seek(index.getStartPos());
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
//        os.getFD().sync();

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

            //time
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
        return hasException ? null : new BitcaskIndex(fileId, valuePos, value == null ? 0 : value.length);
    }

    private void releaseLock(){
        if(lock != null){
            try {
                lock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
            lock = null;
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
