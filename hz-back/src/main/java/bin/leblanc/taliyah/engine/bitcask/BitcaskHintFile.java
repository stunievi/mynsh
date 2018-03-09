package bin.leblanc.taliyah.engine.bitcask;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class BitcaskHintFile extends BitcaskDataFile{

    public BitcaskHintFile(Bitcask context, boolean active) {
        super(context, active);
    }

    public BitcaskHintFile(Bitcask context, boolean active, long fileId) {
        super(context, active, fileId);
    }

    @Override
    protected void init() {
        context.getHintFiles().put(fileId,this);

        filePath = context.getOptions().getDirPath() + File.separator + fileId + context.getOptions().getHintExtension();
        try{
            raf = new RandomAccessFile(filePath,"rw");
            channel = raf.getChannel();

            raf.seek(0);
        }catch (Exception e){

        }
    }

    @Override
    protected void setActive(boolean active) {
        this.active = active;
        if(true == active){
            context.setActiveHintFileId(fileId);
        }
    }


    @Override
    public BitcaskIndex write(byte[] key, byte[] value) {
        return null;
    }

    public synchronized BitcaskIndex readNext(){
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3);
            int readLen = channel.read(byteBuffer);
            if(readLen == -1){
                return null;
            }
            //还要再读n个字节
            int keySize = byteBuffer.getInt(Long.BYTES * 2);
            ByteBuffer keyByteBuffer = ByteBuffer.allocate(keySize);
            channel.read(keyByteBuffer);
            return new BitcaskIndex(
                    byteBuffer.getLong(0),
                    byteBuffer.getLong(Long.BYTES),
                    byteBuffer.getInt(Long.BYTES * 2 + Integer.BYTES),
                    byteBuffer.getInt(Long.BYTES * 2 + Integer.BYTES * 2),
                    keyByteBuffer.array()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(BitcaskIndex index){
        if(!active) return;
        try{
            lock = channel.lock();
            raf.seek(raf.length());
            ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3);
            byteBuffer
                    .putLong(0,index.getTimestamp())
                    .putLong(Long.BYTES,index.getFileId())
                    .putInt(Long.BYTES * 2,index.getKey().length)
                    .putInt(Long.BYTES * 2 + Integer.BYTES,index.getValueSize())
                    .putInt(Long.BYTES * 2 + Integer.BYTES * 2,index.getValuePos());
            channel.write(byteBuffer);
//            //timestamp
//            //fileId
//            //keysize
//            //value size
//            //valuepos
            //key
            channel.write(ByteBuffer.wrap(index.getKey()));
        }
        catch (Exception e){
        }
        finally {
            releaseLock();
        }
    }


    public int getFileSize(){
        try {
            return (int) raf.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return context.getOptions().getMaxFileSize();
    }


}
