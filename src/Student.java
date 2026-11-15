/**
 * Immutable student record for the Lab 7 student directory.
 *
 * Part A of the workbook (Task 3) and Part B (StudentDirectory) both store a
 * Student in a HashMap keyed by the student ID. The record is immutable so that
 * a defensive snapshot of the directory can never expose a mutable record.
 */
public record Student(String id, String name, double grade) {
}
