package carpet.script.bundled;

import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReaders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileModule extends Module
{
    private String name;
    private String code;
    private boolean library;
    public FileModule(File sourceFile)
    {
        library = sourceFile.getName().endsWith(".scl");
        try
        {
            name = sourceFile.getName().replaceFirst("\\.scl?","").toLowerCase(Locale.ROOT);
            code = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
        }
        catch ( IOException e)
        {
            name = null;
            code = null;
        }
    }
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    @Override
    public boolean isLibrary()
    {
        return library;
    }

    //copied private method from net.minecraft.nbt.NbtIo.read()
    public static Tag read(File file)
    {
        try (DataInputStream dataInput_1 = new DataInputStream(new FileInputStream(file)))
        {
            byte byte_1 = dataInput_1.readByte();
            if (byte_1 == 0)
            {
                return null;
            }
            else
            {
                dataInput_1.readUTF();
                return TagReaders.of(byte_1).read(dataInput_1, 0, PositionTracker.DEFAULT);
            }
        }
        catch (IOException ignored)
        {
        }
        return null;
    }

    //copied private method from net.minecraft.nbt.NbtIo.write() and client method safe_write
    public static boolean write(Tag tag_1, File file)
    {
        File file_2 = new File(file.getAbsolutePath() + "_tmp");
        if (file_2.exists()) file_2.delete();

        try(DataOutputStream dataOutputStream_1 = new DataOutputStream(new FileOutputStream(file_2)))
        {
            dataOutputStream_1.writeByte(tag_1.getType());
            if (tag_1.getType() != 0)
            {
                dataOutputStream_1.writeUTF("");
                tag_1.write(dataOutputStream_1);
            }
        }
        catch (IOException e)
        {
            return false;
        }
        if (file.exists()) file.delete();
        if (!file.exists()) file_2.renameTo(file);
        return true;
    }

    public static boolean appendText(File filePath, boolean addNewLines, List<String> data)
    {
        try
        {
            OutputStream out = Files.newOutputStream(filePath.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)))
            {
                for (String line: data)
                {
                    writer.append(line);
                    if (addNewLines) writer.newLine();
                }
            }
        }
        catch (IOException e)
        {
            return false;
        }
        return true;
    }


    public static List<String> listFileContent(File filePath)
    {
        try (BufferedReader reader = Files.newBufferedReader(filePath.toPath(), StandardCharsets.UTF_8)) {
            List<String> result = new ArrayList<>();
            for (;;) {
                String line = reader.readLine();
                if (line == null)
                    break;
                result.add(line.replaceAll("[\n\r]+",""));
            }
            return result;
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
