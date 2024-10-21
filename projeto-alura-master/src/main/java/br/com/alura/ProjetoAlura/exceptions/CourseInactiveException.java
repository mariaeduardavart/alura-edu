package br.com.alura.ProjetoAlura.exceptions;

public class CourseInactiveException extends RuntimeException {
    public CourseInactiveException(String message) {
        super(message);
    }
}