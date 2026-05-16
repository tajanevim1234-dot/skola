package oop.skuska;

public class Student implements Comparable<Student>{
    private int id;
    private String meno;
    private String email;
    private int enrollment_year;

    public Student(int id,String meno,String email,int enrollment_year){
        this.id=id;
        this.meno=meno;
        this.email=email;
        this.enrollment_year=enrollment_year;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return meno;
    }
    public String getEmail() {
        return email;
    }
    public int getYear() {
        return enrollment_year;
    }

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.id, other.id);
    }

    
}
