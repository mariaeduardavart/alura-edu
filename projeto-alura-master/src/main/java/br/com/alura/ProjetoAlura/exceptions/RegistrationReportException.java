package br.com.alura.ProjetoAlura.exceptions;

public class RegistrationReportException extends RuntimeException {

    public RegistrationReportException(String message) {
        super(message);
    }

    public RegistrationReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
