package files;

import java.io.FileWriter;
import java.io.*;

public class DefaultFileWriter implements files.FileWriter {
    private boolean isContentEmpty(String content) {
        return content == null || content.isBlank();
    }

    public void writeFileContent(String filePath, String content, boolean append) throws NullPointerException, IOException {
        if (isContentEmpty(content))
            return;

        File writeFile = new File(filePath);

        // Create file
        if (!writeFile.exists())
            if (!writeFile.createNewFile())
                throw new FileNotFoundException("File %s not found and couldn't be created".formatted(filePath));

        writeFileContent(writeFile, content, append);
    }

    public void writeFileContent(File writeFile, String content, boolean append)
            throws NullPointerException, IOException {
        try {
            try (FileWriter resourceFileWriter = new FileWriter(writeFile, append)) {

                try (BufferedWriter bWriter = new BufferedWriter(resourceFileWriter)) {
                    if (append)
                        bWriter.append("\n%s".formatted(content));

                    else
                        bWriter.append(content);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
