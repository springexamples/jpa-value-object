package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            Stream.of(
                    new User("Ali", HijriDate.of("1438-01-02")),
                    new User("Wael", HijriDate.of("1438-02-22")),
                    new User("Mostafa", HijriDate.of("1439-01-02")),
                    new User("Mortada", HijriDate.of("1444-01-16")),
                    new User("Ibrahim", HijriDate.of("1436-01-05"))
            ).forEach(userRepository::save);

            userRepository.findAll().forEach(o -> System.out.println(o.getName() + ", " + o.getBirthDate().asLocalDate()));
        };
    }
}

interface UserRepository extends JpaRepository<User, Long> {

}

@Embeddable
class HijriDate {

    public static final String DB_DATE_FORMAT = "yyyyMMdd";
    public static final String INPUT_REGEX = "([1]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|30)";   // yyyy-MM-dd

    private int hijriDate;      // stored in the database as Integer value like: 14440116

    static HijriDate of(String hijriDate) {

        if (!hijriDate.matches(INPUT_REGEX)) {
            throw new IllegalArgumentException("invalid hijri date, " + hijriDate + " does not matches " + INPUT_REGEX);
        }

        hijriDate = hijriDate.replaceAll("-", "");
        HijriDate hd = new HijriDate();
        hd.hijriDate = Integer.parseInt(hijriDate);
        return hd;
    }

    public HijriDate() {
    }

    public LocalDate asLocalDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DB_DATE_FORMAT)
                .withChronology(HijrahChronology.INSTANCE);
        return LocalDate.from(dtf.parse(toString()));
    }

    public String toString() {
        return String.valueOf(hijriDate);
    }
}

@Entity
class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private HijriDate birthDate;

    public User() {
    }

    public User(String name, HijriDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HijriDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(HijriDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
