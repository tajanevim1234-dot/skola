package oop.skuska;

import java.util.Set;
import java.util.TreeSet;

public class University2 extends University {

    public String exportStudentsByYear(int year) {
        Set<Student> filteredStudents = new TreeSet<Student>();

        for (Student student : students) {
            if (student.getYear() == year) {
                filteredStudents.add(student);
            }
        }

        return exporter.export(filteredStudents);
    }

    public String exportStudentById(int id) throws StudentNotFoundException {
        for (Student student : students) {
            if (student.getId() == id) {
                return exporter.export(student);
            }
        }

        throw new StudentNotFoundException(id);
    }
    
}
