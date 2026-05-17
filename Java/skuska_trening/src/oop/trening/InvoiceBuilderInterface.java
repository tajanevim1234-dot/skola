package oop.trening;

public interface InvoiceBuilderInterface {
    void setCustomer(String customer);
    void setContent(String content);
    void setTotal(double total);
    void reset();
    Invoice build() throws InvoiceNotBuildableException;
}
