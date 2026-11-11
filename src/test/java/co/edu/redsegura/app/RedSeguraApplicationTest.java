package co.edu.redsegura.app;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RedSeguraApplicationTest {

    private String runWithInput(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        RedSeguraApplication application = new RedSeguraApplication(
                new RedSeguraCore(),
                new BufferedReader(new StringReader(input)),
                output);
        application.runInteractive();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void interactiveMenuExecutesAllMandatoryQueries() {
        String output = runWithInput("1\n2\n3\n4\n5\n6\n7\n0\n");

        assertTrue(output.contains("Q1:"));
        assertTrue(output.contains("Q2:"));
        assertTrue(output.contains("Q3:"));
        assertTrue(output.contains("Q4:"));
        assertTrue(output.contains("Q5:"));
        assertTrue(output.contains("Q6:"));
        assertTrue(output.contains("Q7:"));
        assertTrue(output.contains("24 minutos"));
        assertTrue(output.contains("LISTA DE ADYACENCIA"));
        assertTrue(output.contains("MATRIZ DE INCIDENCIA"));
        assertTrue(output.contains("Caso LL"));
        assertTrue(output.contains("Caso RR"));
        assertTrue(output.contains("Caso LR"));
        assertTrue(output.contains("Caso RL"));
        assertTrue(output.contains("S99"));
        assertTrue(output.contains("Fin de la sesión RedSegura"));
    }

    @Test
    void nonexistentVertexDoesNotTerminateInteractiveSession() {
        String output = runWithInput("7\n0\n");

        assertTrue(output.contains("[ERROR CONTROLADO]"));
        assertTrue(output.contains("el proceso no termina"));
        assertTrue(output.contains("Fin de la sesión RedSegura"));
    }

    @Test
    void invalidMenuOptionIsReportedAndSessionContinues() {
        String output = runWithInput("x\n0\n");

        assertTrue(output.contains("Opción no válida"));
        assertTrue(output.contains("Fin de la sesión RedSegura"));
    }
}
