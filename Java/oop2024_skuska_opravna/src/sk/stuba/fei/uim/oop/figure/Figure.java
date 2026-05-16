package sk.stuba.fei.uim.oop.figure;

// Figurka alebo dekorator 
public interface Figure {
    
    // Posunie sa o urcity pocet krokov (pocet krokov urcuje implementacia podtypu)
    void move();

    // Vrati poziciu (startnovacia pozicia je 0)
    int getPosition();

    // Nastavi poziciu
    void setPosition(int position);
}
