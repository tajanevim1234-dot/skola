package oop.skuska;

import java.util.Set;

public class VCardContactExporter implements ContactExporter {

    @Override
    public String export(Student student) {
        return "BEGIN:VCARD\n"
        + "FN:" + student.getName() + "\n"
        + "EMAIL:" + student.getEmail() + "\n"
        + "END:VCARD\n";
    }

    @Override
    public String export(Set<Student> students) {
        String result = "";
        for (Student student : students) {
            result += export(student);
        }
        return result;
    }
}
