package oop.skuska;

import java.util.Set;
import java.util.TreeSet;

public class University {

    protected Set<Student> students;
    protected ContactExporter exporter;

    public University() {
        students = new TreeSet<Student>();
    }

    public void addStudent(Student student){
        students.add(student);
    }

    public void setExporter(ContactExporter exporter) {
        this.exporter = exporter;
    }

    public String exportStudents() {
        return exporter.export(students);
    }

}
