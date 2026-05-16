package oop.skuska;

import java.util.Set;

public interface ContactExporter {
    String export(Student student);
    String export(Set<Student> students);
}
