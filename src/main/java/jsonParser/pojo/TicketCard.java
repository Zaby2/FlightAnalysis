package jsonParser.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TicketCard {
    private List<Ticket> tickets = new ArrayList<>();
}
