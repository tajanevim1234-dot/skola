package oop.trening;

public class InvoiceBuilder implements InvoiceBuilderInterface {
    private String customer;
    private String content;
    private double total;

    @Override
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public void reset() {
        // TODO
    }

    @Override
    public Invoice build() throws InvoiceNotBuildableException {
        // TODO
        return new Invoice(customer, content, total);
    }
}
