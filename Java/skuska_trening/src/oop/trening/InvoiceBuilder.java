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
        this.customer = null;
        this.content = null;
        this.total = 0;
    }

    @Override
    public Invoice build() throws InvoiceNotBuildableException {
        if(this.customer==null || this.content==null || this.total<=0){
            throw new InvoiceNotBuildableException("smola");
        }
        Invoice invoice = new Invoice(this.customer, this.content, this.total);
        this.reset();
        return invoice;
    }
}
