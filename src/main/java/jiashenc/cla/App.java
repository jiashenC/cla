package jiashenc.cla;

import org.apache.sysml.runtime.compress.CompressedMatrixBlock;
import org.apache.sysml.runtime.io.MatrixReader;
import org.apache.sysml.runtime.io.MatrixReaderFactory;
import org.apache.sysml.runtime.matrix.data.InputInfo;
import org.apache.sysml.runtime.matrix.data.MatrixBlock;

import java.util.Scanner;
import java.io.File;

public class App
{
    public static void main( String[] args ) throws Exception
    {
        String dirPath = args[0];
        int row = Integer.parseInt(args[1]), col = Integer.parseInt(args[2]);

        double originalSize = 0, compressedSize = 0;

        File dir = new File(dirPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String filePath = child.getAbsolutePath();

                // read matrix
                MatrixReader matrixReader = MatrixReaderFactory.createMatrixReader(InputInfo.CSVInputInfo);
                MatrixBlock mb = matrixReader.readMatrixFromHDFS(filePath, row, col, 3, 3, 6);

                originalSize += mb.getExactSizeOnDisk() / 1024.0;

                // compress matrix
                CompressedMatrixBlock cmb = new CompressedMatrixBlock(mb);
                cmb.compress();

                compressedSize += cmb.getExactSizeOnDisk() / 1024.0;
            }

            // print out stats
            System.out.println(String.format("Original: %.2f KB, Compressed: %.2f KB", originalSize, compressedSize));
            System.out.println(String.format("Average compression ratio: %.2f", 1 / (compressedSize / originalSize)));
        } else {
            System.out.println(dirPath + " directory does not exist or is not a directory type.");
        }
    }
}
