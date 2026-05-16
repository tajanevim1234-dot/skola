package oop.skuska;

import java.util.Set;

public class CsvContactExporter implements ContactExporter {

    @Override
    public String export(Student student) {
        return student.getName() + "; " + student.getEmail() + "\n";
    }

    @Override
    public String export(Set<Student> students) {
        String result = "name; email\n";
        for (Student student : students) {
            result += export(student);
        }
        return result;
    }
}
