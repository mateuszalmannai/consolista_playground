package org.ismskism.springshell.commands.newapi;

import org.ismskism.springshell.service.CompressionService;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.context.InteractionMode;

@Command(group = "Compression")
public class ZipperCommand {
  private final CompressionService compressionService;

  public ZipperCommand(CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  @Command(command = "compress", description = "compresses file or folder", interactionMode = InteractionMode.NONINTERACTIVE)
  public String compress(
      @Option(longNames = "input", description = "file or folder to be compressed", shortNames = 'i') String inputPath,
      @Option(longNames = "output", description = "compressed file name", shortNames = 'o') String outputPath
  ) {
    compressionService.zipTarget(inputPath, outputPath);
    return String.format("Input %s successfully compressed to %s" + System.lineSeparator(),
        inputPath,
        outputPath);
  }

  @Command(command = "decompress", description = "decompresses file or folder", interactionMode = InteractionMode.NONINTERACTIVE)
  public void decompress(
      @Option(longNames = "input", description = "file or folder to be decompressed", shortNames = 'i') String inputPath,
      @Option(longNames = "output", description = "folder where the decompressed files will be put in", shortNames = 'o') String outputPath
  ) {
    compressionService.unzipFile(inputPath, outputPath);
    System.out.printf("Input %s successfully decompressed to %s" + System.lineSeparator(),
        inputPath,
        outputPath);
  }

}
