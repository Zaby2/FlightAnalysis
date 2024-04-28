package jsonParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import jsonParser.pojo.Ticket;
import jsonParser.pojo.TicketCard;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import javax.xml.transform.Source;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FlightAnalysis {

    @SneakyThrows
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("C:\\Users\\KK\\IdeaProjects\\JsonTicketParser\\src\\main\\resources\\tickets.json");
        String json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
        json= json.replace("\uFEFF", "");
        TicketCard tickets = objectMapper.readValue(json, TicketCard.class);
        //System.out.println(tickets.getTickets().size());
        tickets.setTickets(tickets.getTickets()
                .stream()
                .filter(ticket -> ticket.getOrigin().equals("VVO"))
                .filter(ticket -> ticket.getDestination().equals("TLV"))
                .collect(Collectors.toList()));
       // System.out.println(tickets.getTickets().size());
        Map<String, List<Long>> durationMap = new HashMap<>();
        int avgVal = 0;
        List<Integer> prices = new ArrayList<>();
        for(Ticket ticket : tickets.getTickets()) {
            avgVal += ticket.getPrice();
            prices.add(ticket.getPrice());
            if(durationMap.getOrDefault(ticket.getCarrier(), null) == null) {
                durationMap.put(ticket.getCarrier(), getFlightTime(ticket));
            } else {
                List<Long> curDur = getFlightTime(ticket);
                if(durationMap.get(ticket.getCarrier()).get(0) >curDur.get(0)) {
                    durationMap.put(ticket.getCarrier(), curDur);
                } else if(durationMap.get(ticket.getCarrier()).get(0) == curDur.get(0)) {
                    if(durationMap.get(ticket.getCarrier()).get(1) > curDur.get(1)) {
                        durationMap.put(ticket.getCarrier(), curDur);
                    }
                }
            }


        }
        Collections.sort(prices);
        int median;
        int totalElements = prices.size();
        if (totalElements % 2 == 0) {
            int sumOfMiddleElements = prices.get(totalElements / 2) + prices.get(totalElements / 2 - 1);
            median = sumOfMiddleElements / 2;
        } else {
            median = prices.get(totalElements / 2);
        }
        System.out.println("Минимальное время полета между городами\n" +
                "Владивосток и Тель-Авив\n" +
                "авиаперевозчика" + durationMap);
        int res = avgVal/tickets.getTickets().size() - median;
                System.out.println("Разницу между средней ценой и медианой для\n" +
                        "полета между городами  Владивосток и Тель-Авив " + res);
    }

    public static List<Long> getFlightTime(Ticket ticket) {
        List<Long> dur  = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String depDateAndTime;
        String arrDateAndTime;
        if(ticket.getDeparture_time().length() == 5) {
             depDateAndTime = ticket.getDeparture_date() + " " + ticket.getDeparture_time();
        } else {
             depDateAndTime = ticket.getDeparture_date() + " 0" + ticket.getDeparture_time();
        }
        if(ticket.getArrival_time().length() == 5) {
            arrDateAndTime = ticket.getArrival_date() + " " + ticket.getArrival_time();
        } else {
            arrDateAndTime = ticket.getArrival_date() + " 0" + ticket.getArrival_time();
        }
        LocalDateTime departure = LocalDateTime.parse(depDateAndTime, dateTimeFormatter);
        LocalDateTime arrival= LocalDateTime.parse(arrDateAndTime, dateTimeFormatter);
        Duration duration = Duration.between(departure, arrival);
        Long hours = duration.toHours();
        Long minutes = (duration.toSeconds() % 3600) /60;
        dur.add(hours);
        dur.add(minutes);
        return dur;
    }
}
