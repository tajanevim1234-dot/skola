package oop.skuska;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllTests {

    Set<Student> createStudents() {
        Set<Student> s = new TreeSet<Student>();
        s.add(new Student(100, "John", "john@stuba.sk", 2021));
        s.add(new Student(101, "Andrej", "andrej@stuba.sk", 2020));
        s.add(new Student(102, "Peter", "peter@stuba.sk", 2025));
        s.add(new Student(103, "Ondrej", "ondrej@stuba.sk",  2024));
        s.add(new Student(104, "Milan", "milan@stuba.sk", 2022));
        return s;
    }

    // --------- Student --------------------------------------------------------------------------

    @Test
    void studentAttribute_1() {
        Field[] fields = Student.class.getDeclaredFields();
        assertEquals(4, fields.length, "Student musi mat 4 atributy");
        for (Field f: fields) {
            assertTrue(Modifier.isPrivate(f.getModifiers()), "Student musi obsahovat len private atributy");
        }
    }

    @Test
    void studentConstructorAndGetters_4() {
        Student s1 = new Student(1000, "Jan", "jan@stuba.sk", 2024);
        assertEquals(1000, s1.getId(), "Chyba v ID studenta");
        assertEquals("Jan", s1.getName(), "Chybne meno studenta");
        assertEquals("jan@stuba.sk", s1.getEmail(), "Chybny email studenta");
        assertEquals(2024, s1.getYear(), "Chybny rok nastupu studenta");

        Student s2 = new Student(1001, "Riso Vypoctar", "vypoctar@stuba.sk", 2020);
        assertEquals(1001, s2.getId(), "Chyba v ID studenta");
        assertEquals("Riso Vypoctar", s2.getName(), "Chybne meno studenta");
        assertEquals("vypoctar@stuba.sk", s2.getEmail(), "Chybny email studenta");
        assertEquals(2020, s2.getYear(), "Chybny rok nastupu studenta");
    }

    @Test
    void studentIsSortable_2() {
        Class<?> interfaces[] = Student.class.getInterfaces();
        assertEquals(1, interfaces.length, "Student musi implementovat jeden interface");
        assertTrue(interfaces[0].isAssignableFrom(Comparable.class), "Student musi implementovat comparable");
    }

    @Test
    void studentSort_2() {
        Student s1 = new Student(100, "ccc", "ccc@stuba.sk", 2023);
        Student s2 = new Student(200, "bbb", "bbb@stuba.sk", 2022);
        Student s3 = new Student(200, "bbb", "bbb@stuba.sk", 2022);
        Student s4 = new Student(300, "aaa", "aaa@stuba.sk", 2021);

        assertTrue(s2.compareTo(s1) > 0,  "Student treba porovnavat podla ID (predpokladame, ze kazdy student ma jedinecne ID)");
        assertTrue(s2.compareTo(s3) == 0, "Student treba porovnavat podla ID (predpokladame, ze kazdy student ma jedinecne ID)");
        assertTrue(s2.compareTo(s4) < 0,  "Student treba porovnavat podla ID (predpokladame, ze kazdy student ma jedinecne ID)");
    }

    // ----------- exporter csv -------------------------------------------------------------------

    @Test
    void csvOne_5() {
        String expected = """
                Ivan Inventor; ivan@stuba.sk
                """;
        ContactExporter e = new CsvContactExporter();
        Student s = new Student(10, "Ivan Inventor", "ivan@stuba.sk", 2024);
        String csv = e.export(s);
        assertEquals(expected, csv);
    }

    @Test
    void csvAll_5() {
        String expected = """
                name; email
                John; john@stuba.sk
                Andrej; andrej@stuba.sk
                Peter; peter@stuba.sk
                Ondrej; ondrej@stuba.sk
                Milan; milan@stuba.sk
                """;
        ContactExporter e = new CsvContactExporter();
        Set<Student> s = createStudents();
        String csv = e.export(s);
        assertEquals(expected, csv);
    }

    // ----------- exporter vCard -----------------------------------------------------------------

    @Test
    void vCardOne_5() {
        String expected = """
            BEGIN:VCARD
            FN:Tristan T
            EMAIL:tristan@stuba.sk
            END:VCARD
            """;
        ContactExporter e = new VCardContactExporter();
        Student s = new Student(10, "Tristan T", "tristan@stuba.sk", 2024);
        String vCard = e.export(s);
        assertEquals(expected, vCard);
    }

    @Test
    void vCardAll_5() {
        String expected = """
                BEGIN:VCARD
                FN:John
                EMAIL:john@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Andrej
                EMAIL:andrej@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Peter
                EMAIL:peter@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Ondrej
                EMAIL:ondrej@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Milan
                EMAIL:milan@stuba.sk
                END:VCARD
                """;
        ContactExporter e = new VCardContactExporter();
        Set<Student> s = createStudents();
        String vCard = e.export(s);
        assertEquals(expected, vCard);
    }

    // ---------- university ----------------------------------------------------------------------

    @Test
    void universityAttrSet_1() {
        Field[] fields = University.class.getDeclaredFields();

        long setCount = Arrays.stream(fields)
                .filter(attr -> attr.getType() == Set.class )
                .count();
        assertEquals(1, setCount, "Mnozina studentov v University musi byt typu Set");

        long setCount2 = Arrays.stream(fields)
                .filter(attr -> Set.class.isAssignableFrom(attr.getType()))
                .count();
        assertEquals(1, setCount2, "University musi obsahovat prave jednu mnozinu (studentov)");
    }

    @Test
    void universityAttrExporter_1() {
        Field[] fields = University.class.getDeclaredFields();

        long setCount2 = Arrays.stream(fields)
                .filter(attr -> ContactExporter.class.isAssignableFrom(attr.getType()))
                .count();
        assertEquals(1, setCount2, "University musi obsahovat prave jeden exporter");
    }

    @Test
    void universityExportCsv_4() {
        University u = new University();
        u.setExporter(new CsvContactExporter());

        String csvEmpty = """
                name; email
                """;
        assertEquals(csvEmpty, u.exportStudents());

        String csvOne = """
                name; email
                Bea; bea@stuba.sk
                """;
        u.addStudent(new Student(20, "Bea", "bea@stuba.sk", 2024));
        assertEquals(csvOne, u.exportStudents());

        String csvTwo = """
                name; email
                Bea; bea@stuba.sk
                Tea; tea@stuba.sk
                """;
        u.addStudent(new Student(30, "Tea", "tea@stuba.sk", 2023));
        assertEquals(csvTwo, u.exportStudents());

        String csvThree = """
                name; email
                Ria; ria@stuba.sk
                Bea; bea@stuba.sk
                Tea; tea@stuba.sk
                """;
        u.addStudent(new Student(10, "Ria", "ria@stuba.sk", 2023));
        assertEquals(csvThree, u.exportStudents());

        String csvFour = """
                name; email
                Mia; mia@stuba.sk
                Ria; ria@stuba.sk
                Bea; bea@stuba.sk
                Tea; tea@stuba.sk
                """;
        u.addStudent(new Student(5, "Mia", "mia@stuba.sk", 2025));
        assertEquals(csvFour, u.exportStudents());
    }

    @Test
    void univesityExportVCard_4() {
        University u = new University();
        u.setExporter(new VCardContactExporter());

        String vCard0 = """
                """;
        assertEquals(vCard0, u.exportStudents());

        String vCard1 = """
                BEGIN:VCARD
                FN:Bea
                EMAIL:bea@stuba.sk
                END:VCARD
                """;
        u.addStudent(new Student(20, "Bea", "bea@stuba.sk", 2024));
        assertEquals(vCard1, u.exportStudents());

        String vCard2 = """
                BEGIN:VCARD
                FN:Bea
                EMAIL:bea@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Tea
                EMAIL:tea@stuba.sk
                END:VCARD
                """;
        u.addStudent(new Student(30, "Tea", "tea@stuba.sk", 2023));
        assertEquals(vCard2, u.exportStudents());

        String vCard3 = """
                BEGIN:VCARD
                FN:Ria
                EMAIL:ria@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Bea
                EMAIL:bea@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Tea
                EMAIL:tea@stuba.sk
                END:VCARD
                """;
        u.addStudent(new Student(10, "Ria", "ria@stuba.sk", 2023));
        assertEquals(vCard3, u.exportStudents());

        String vCard4 = """
                BEGIN:VCARD
                FN:Mia
                EMAIL:mia@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Ria
                EMAIL:ria@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Bea
                EMAIL:bea@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Tea
                EMAIL:tea@stuba.sk
                END:VCARD
                """;
        u.addStudent(new Student(5, "Mia", "mia@stuba.sk", 2025));
        assertEquals(vCard4, u.exportStudents());
    }

    @Test
    void universitySwitchExporter_4() {
        University u = new University();
        for (Student s: createStudents()) {
            u.addStudent(s);
        }

        String csv = """
                name; email
                John; john@stuba.sk
                Andrej; andrej@stuba.sk
                Peter; peter@stuba.sk
                Ondrej; ondrej@stuba.sk
                Milan; milan@stuba.sk
                """;
        String vCard = """
                BEGIN:VCARD
                FN:John
                EMAIL:john@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Andrej
                EMAIL:andrej@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Peter
                EMAIL:peter@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Ondrej
                EMAIL:ondrej@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Milan
                EMAIL:milan@stuba.sk
                END:VCARD
                """;

        // testovanie zmeny exportera

        u.setExporter(new CsvContactExporter());
        assertEquals(csv, u.exportStudents());

        u.setExporter(new VCardContactExporter());
        assertEquals(vCard, u.exportStudents());

        u.setExporter(new CsvContactExporter());
        assertEquals(csv, u.exportStudents());

        u.setExporter(new VCardContactExporter());
        assertEquals(vCard, u.exportStudents());
    }

    // ---------- university 2 --------------------------------------------------------------------

    @Test
    void university2_1() {
        assertEquals(University2.class.getSuperclass(), University.class, "University2 musi mat priamu nadtriedu University");
    }

    @Test
    void university2ExportByYearCsv_3(){
        University2 u2 = new University2();
        u2.setExporter(new CsvContactExporter());

        for (Student s: createStudents()) {
            u2.addStudent(s);
        }
        u2.addStudent(new Student(105, "Emil", "emil@stuba.sk", 2020));
        u2.addStudent(new Student(106, "Jozef", "jozef@stuba.sk", 2024));
        u2.addStudent(new Student(107, "Juraj", "juraj@stuba.sk", 2020));

        String csv2020 = """
                name; email
                Andrej; andrej@stuba.sk
                Emil; emil@stuba.sk
                Juraj; juraj@stuba.sk
                """;
        assertEquals(csv2020, u2.exportStudentsByYear(2020));

        String csv2024 = """
                name; email
                Ondrej; ondrej@stuba.sk
                Jozef; jozef@stuba.sk
                """;
        assertEquals(csv2024, u2.exportStudentsByYear(2024));

        String csv2025 = """
                name; email
                Peter; peter@stuba.sk
                """;
        assertEquals(csv2025, u2.exportStudentsByYear(2025));

        String csv2000 = """
                name; email
                """;
        assertEquals(csv2000, u2.exportStudentsByYear(2000));
    }

    @Test
    void university2ExportByYearVCard_3() {
        University2 u2 = new University2();
        u2.setExporter(new VCardContactExporter());

        for (Student s : createStudents()) {
            u2.addStudent(s);
        }
        u2.addStudent(new Student(105, "Emil", "emil@stuba.sk", 2020));
        u2.addStudent(new Student(106, "Jozef", "jozef@stuba.sk", 2024));
        u2.addStudent(new Student(107, "Juraj", "juraj@stuba.sk", 2020));

        String vCard2020 = """
                BEGIN:VCARD
                FN:Andrej
                EMAIL:andrej@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Emil
                EMAIL:emil@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Juraj
                EMAIL:juraj@stuba.sk
                END:VCARD
                """;
        assertEquals(vCard2020, u2.exportStudentsByYear(2020));

        String vCard2024 = """
                BEGIN:VCARD
                FN:Ondrej
                EMAIL:ondrej@stuba.sk
                END:VCARD
                BEGIN:VCARD
                FN:Jozef
                EMAIL:jozef@stuba.sk
                END:VCARD
                """;
        assertEquals(vCard2024, u2.exportStudentsByYear(2024));

        String vCard20025 = """
                BEGIN:VCARD
                FN:Peter
                EMAIL:peter@stuba.sk
                END:VCARD
                """;
        assertEquals(vCard20025, u2.exportStudentsByYear(2025));

        String vCard2000 = """
                """;
        assertEquals(vCard2000, u2.exportStudentsByYear(2000));
    }

    @Test
    void university2ExportByIdCsv_3() throws StudentNotFoundException {
        University2 u2 = new University2();
        u2.setExporter(new CsvContactExporter());

        for (Student s: createStudents()) {
            u2.addStudent(s);
        }

        String andrej = """
                Andrej; andrej@stuba.sk
                """;
        assertEquals(andrej, u2.exportStudentById(101));

        String milan = """
                Milan; milan@stuba.sk
                """;
        assertEquals(milan, u2.exportStudentById(104));

        String peter = """
            Peter; peter@stuba.sk
            """;
        assertEquals(peter, u2.exportStudentById(102));
    }

    @Test
    void university2ExportByIdVCard_3() throws StudentNotFoundException {
        University2 u2 = new University2();
        u2.setExporter(new VCardContactExporter());

        for (Student s: createStudents()) {
            u2.addStudent(s);
        }

        String andrej = """
                BEGIN:VCARD
                FN:Andrej
                EMAIL:andrej@stuba.sk
                END:VCARD
                """;
        assertEquals(andrej, u2.exportStudentById(101));

        String milan = """
                BEGIN:VCARD
                FN:Milan
                EMAIL:milan@stuba.sk
                END:VCARD
                """;
        assertEquals(milan, u2.exportStudentById(104));

        String peter = """
                BEGIN:VCARD
                FN:Peter
                EMAIL:peter@stuba.sk
                END:VCARD
                """;
        assertEquals(peter, u2.exportStudentById(102));
    }

    @Test
    void university2ExportByIdCsvException_2() {
        University2 u2 = new University2();
        u2.setExporter(new CsvContactExporter());

        u2.addStudent(new Student(100, "John", "john@stuba.sk", 2021));
        u2.addStudent(new Student(101, "Andrej", "andrej@stuba.sk", 2020));
        u2.addStudent(new Student(103, "Ondrej", "ondrej@stuba.sk",  2024));
        u2.addStudent(new Student(104, "Milan", "milan@stuba.sk", 2022));

        assertThrows(StudentNotFoundException.class, () -> u2.exportStudentById(102));
        assertThrows(StudentNotFoundException.class, () -> u2.exportStudentById(10));
        assertThrows(StudentNotFoundException.class, () -> u2.exportStudentById(105));
    }

    @Test
    void university2ExportByIdVCardException_2() {
        University2 u2 = new University2();
        u2.setExporter(new VCardContactExporter());

        u2.addStudent(new Student(100, "John", "john@stuba.sk", 2021));
        u2.addStudent(new Student(101, "Andrej", "andrej@stuba.sk", 2020));
        u2.addStudent(new Student(103, "Ondrej", "ondrej@stuba.sk",  2024));
        u2.addStudent(new Student(104, "Milan", "milan@stuba.sk", 2022));

        assertThrows(StudentNotFoundException.class, () -> u2.exportStudentById(102));
        assertThrows(StudentNotFoundException.class, () -> u2.exportStudentById(10));
        assertThrows(StudentNotFoundException.class, () -> u2.exportStudentById(105));
    }
}
