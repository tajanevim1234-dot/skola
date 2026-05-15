package test;

import java.util.ArrayList;
import java.util.List;

public class ReservationSystem {
    private List<Reservation> Reservations = new ArrayList<>();

    public void addReservation(Reservation r) throws DuplicateReservationException{
        if(Reservations.contains(r)){
            throw new DuplicateReservationException("si pici");
        }
        Reservations.add(r);
    }

    public void cancelReservation(String Id) throws ReservationNotFoundException{
        for(Reservation n : Reservations){
            if(n.getResevationId().equals(Id)){
                Reservations.remove(n);
                return;
            }
        }
        throw new ReservationNotFoundException(Id +"neexistujes");

        
    } 

    public int getCount(){
        return Reservations.size();
    }

    public double getTotalRenevue(){
        double sum=0;
        for(Reservation n : Reservations){
            sum=sum+n.getTotalPrice();
        }
        return sum;
    }

}
