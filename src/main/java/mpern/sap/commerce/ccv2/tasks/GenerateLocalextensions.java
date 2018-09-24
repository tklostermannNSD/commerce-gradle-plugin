package mpern.sap.commerce.ccv2.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Set;

public class GenerateLocalextensions extends DefaultTask {

    private static final String START = "<hybrisconfig xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='../bin/platform/resources/schemas/extensions.xsd'>\n" +
            "  <extensions>\n" +
            "    <path dir='${HYBRIS_BIN_DIR}' autoload='false' />\n";
    private static final String END = "  </extensions>\n" +
            "</hybrisconfig>";

    private static final String EXTENSION = "    <extension name='%s' />\n";

    private final RegularFileProperty target;
    private final SetProperty<String> cloudExtensions;

    public GenerateLocalextensions() {
        target = newOutputFile();
        cloudExtensions = getProject().getObjects().setProperty(String.class);
    }

    @TaskAction
    public void generateLocalextensions() throws IOException {
        Path target = getTarget().get().getAsFile().toPath();
        Set<String> extensions = cloudExtensions.get();
        try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(START);
            writer.write(String.format("\n<!-- GENERATED by task %s at %s -->\n\n", getName(), Instant.now()));
            for (String extension : extensions) {
                writer.write(String.format(EXTENSION, extension));
            }
            writer.write(END);
        }
    }

    @OutputFile
    public RegularFileProperty getTarget() {
        return target;
    }

    @Input
    public SetProperty<String> getCloudExtensions() {
        return cloudExtensions;
    }
}