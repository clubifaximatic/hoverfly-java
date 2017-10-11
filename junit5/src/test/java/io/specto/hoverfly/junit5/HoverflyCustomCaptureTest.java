package io.specto.hoverfly.junit5;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit5.api.HoverflyCapture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.specto.hoverfly.junit5.api.HoverflyConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HoverflyCustomCaptureTest {

    private static Hoverfly hoverflyInstance;
    private static final Path CAPTURED_SIMULATION_FILE = Paths.get("build/resources/test/hoverfly/captured-simulation.json");

    @BeforeAll
    static void cleanUpPreviousCapturedFile() throws IOException {
        Files.deleteIfExists(CAPTURED_SIMULATION_FILE);
    }

    @Nested
    @HoverflyCapture(path = "build/resources/test/hoverfly",
            filename = "captured-simulation.json",
            config = @HoverflyConfig(destination = "hoverfly.io"))
    @ExtendWith(HoverflyCaptureResolver.class)
    class NestedTest {

        @Test
        void shouldInjectCustomInstanceAsParameter(Hoverfly hoverfly) {
            hoverflyInstance = hoverfly;
            assertThat(hoverfly.getMode()).isEqualTo(HoverflyMode.CAPTURE);
            assertThat(hoverfly.getHoverflyInfo().getModeArguments().getHeadersWhitelist()).isNull();   // Not capturing any request headers
            assertThat(hoverfly.getHoverflyInfo().getDestination()).isEqualTo("hoverfly.io");     // Capture all destinations
        }
    }


    @AfterAll
    static void shouldExportCapturedSimulation() {
        assertThat(hoverflyInstance.isHealthy()).isFalse();
        assertThat(CAPTURED_SIMULATION_FILE).exists();
    }
}