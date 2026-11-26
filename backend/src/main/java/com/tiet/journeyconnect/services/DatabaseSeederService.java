package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Seeds static campus data (Destinations, Hostels, Communities) and pre-configured
 * test student accounts into the embedded H2 database at startup.
 */
@Service
public class DatabaseSeederService implements CommandLineRunner {

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDestinations();
        seedHostels();
        seedCommunities();
        seedTestAccounts();
    }

    private void seedDestinations() {
        if (destinationRepository.count() == 0) {
            destinationRepository.saveAll(List.of(
                    new DestinationEntity("railway", "Patiala Railway Station", "🚆", "Find students travelling from campus to Patiala Railway Station.", "Peak: 4:00 PM - 8:00 PM", "8.5 km from Main Gate", "₹150 - ₹200 total"),
                    new DestinationEntity("bus_stand", "Patiala Bus Stand", "🚌", "Find students travelling from campus to Patiala Bus Stand for intercity buses.", "Peak: 6:00 AM - 10:00 AM", "6.2 km from Main Gate", "₹120 - ₹160 total"),
                    new DestinationEntity("chd_airport", "Chandigarh Airport", "✈️", "Coordinate rides for flights departing from Shaheed Bhagat Singh Airport.", "Peak: Morning & Evening Flights", "62 km from Main Gate", "₹1,200 - ₹1,500 total"),
                    new DestinationEntity("del_airport", "Delhi Airport", "✈️", "Connect with students taking long-distance cabs or trains to IGI Airport.", "Peak: Overnight & Early Morning", "250 km from Main Gate", "₹4,500 - ₹5,500 total")
            ));
        }
    }

    private void seedHostels() {
        if (hostelRepository.count() == 0) {
            hostelRepository.saveAll(List.of(
                    // Boys Hostels
                    new HostelEntity("HA", "Agira Hall (Previously known as Hostel-A)", "Boys", "North Quad"),
                    new HostelEntity("HB", "Amritam Hall (Previously known as Hostel-B)", "Boys", "North Quad"),
                    new HostelEntity("HC", "Prithvi Hall (Previously known as Hostel-C)", "Boys", "North Quad"),
                    new HostelEntity("HD", "Neeram Hall (Previously known as Hostel-D)", "Boys", "Central Quad"),
                    new HostelEntity("HH", "Vyan Hall (Previously known as Hostel-H)", "Boys", "Central Quad"),
                    new HostelEntity("HJ", "Tejas Hall (Previously known as Hostel-J)", "Boys", "East Quad"),
                    new HostelEntity("HK", "Ambaram Hall (Previously known as Hostel-K)", "Boys", "East Quad"),
                    new HostelEntity("HL", "Viyat Hall (Previously known as Hostel-L)", "Boys", "East Quad"),
                    new HostelEntity("HM", "Anantam Hall (Previously known as Hostel-M)", "Boys", "West Quad"),
                    new HostelEntity("HO", "Vyom Hall (Previously known as Hostel-O)", "Boys", "West Quad"),
                    new HostelEntity("FRFG", "Hostel-FRF/G", "Boys", "West Quad"),

                    // Girls Hostels
                    new HostelEntity("HE", "Vasudha Hall - Block E (Previously known as Hostel-E)", "Girls", "Vasudha Complex"),
                    new HostelEntity("HG", "Vasudha Hall - Block G (Previously known as Hostel-G)", "Girls", "Vasudha Complex"),
                    new HostelEntity("HI", "Ira Hall (Previously known as Hostel-I)", "Girls", "Girls Quad"),
                    new HostelEntity("HN", "Ananta Hall (Previously known as Hostel-N)", "Girls", "Girls Quad"),
                    new HostelEntity("Dhriti", "Dhriti Hall (Hostel-PG)", "Girls", "Girls Quad"),
                    new HostelEntity("HQ", "Vahni Hall (Previously known as Hostel-Q)", "Girls", "Girls Quad"),
                    new HostelEntity("Pavani", "Hostel-Pavani Hall (Previously Known As PG-2)", "Girls", "Girls Quad")
            ));
        }
    }

    private void seedCommunities() {
        if (communityRepository.count() == 0) {
            CommunityEntity del = new CommunityEntity();
            del.setId("del");
            del.setName("Delhi NCR");
            del.setState("Delhi / NCR");
            del.setIcon("🚇");
            del.setDistanceFromThapar("250 km");
            del.setPopularTransport("Shatabdi Express, Long Cabs, Volvos");
            del.setDescription("Student network for South Delhi, Noida, Gurgaon, West Delhi & IGI Airport transit.");
            del.setTags("Holiday Rush,Shatabdi Cohort,IGI Airport");
            del.setMembersCount(2);

            CommunityEntity jal = new CommunityEntity();
            jal.setId("jal");
            jal.setName("Jalandhar");
            jal.setState("Punjab");
            jal.setIcon("🏛️");
            jal.setDistanceFromThapar("145 km");
            jal.setPopularTransport("GT Road Cabs, Intercity Trains");
            jal.setDescription("Student hub for Jalandhar, Phagwara & Doaba region travelers.");
            jal.setTags("Weekly Commuters,Cab Sharing,GT Road");

            CommunityEntity ldh = new CommunityEntity();
            ldh.setId("ldh");
            ldh.setName("Ludhiana");
            ldh.setState("Punjab");
            ldh.setIcon("🏭");
            ldh.setDistanceFromThapar("95 km");
            ldh.setPopularTransport("Direct Cabs, Passenger Trains");
            ldh.setDescription("Commute corridor for Ludhiana travelers sharing cabs via GT Road.");
            ldh.setTags("Weekend Trips,Quick Ride,Day Scholars");

            CommunityEntity chd = new CommunityEntity();
            chd.setId("chd");
            chd.setName("Chandigarh & Tri-City");
            chd.setState("Chandigarh / Mohali / Panchkula");
            chd.setIcon("🏙️");
            chd.setDistanceFromThapar("65 km");
            chd.setPopularTransport("CTU Buses, Shared Cabs, Personal Cars");
            chd.setDescription("Travelers heading towards Mohali, Chandigarh Sector 17/43, and Panchkula.");
            chd.setTags("Daily/Weekly,Mohali IT,Airport Cabs");

            CommunityEntity asr = new CommunityEntity();
            asr.setId("asr");
            asr.setName("Amritsar");
            asr.setState("Punjab");
            asr.setIcon("🛕");
            asr.setDistanceFromThapar("225 km");
            asr.setPopularTransport("Express Trains (Shan-e-Punjab), Highway Cabs");
            asr.setDescription("Connecting students heading towards Amritsar and Majha belt.");
            asr.setTags("Long Weekend,Festival Travel,Trains");

            CommunityEntity bth = new CommunityEntity();
            bth.setId("bth");
            bth.setName("Bathinda");
            bth.setState("Punjab");
            bth.setIcon("⚡");
            bth.setDistanceFromThapar("155 km");
            bth.setPopularTransport("PRTC Buses, Local Trains");
            bth.setDescription("Malwa region student cohort sharing weekend travel.");
            bth.setTags("Malwa Cohort,Weekend Trips");

            CommunityEntity jpr = new CommunityEntity();
            jpr.setId("jpr");
            jpr.setName("Jaipur");
            jpr.setState("Rajasthan");
            jpr.setIcon("🏰");
            jpr.setDistanceFromThapar("470 km");
            jpr.setPopularTransport("Overnight Buses, Intercity Trains");
            jpr.setDescription("Long-distance cohort coordinating semester breaks and Diwali holiday trips to Pink City.");
            jpr.setTags("Semester Breaks,Holiday Trains");

            communityRepository.saveAll(List.of(del, jal, ldh, chd, asr, bth, jpr));
        }
    }

    private void seedTestAccounts() {
        String testPasswordHash = passwordEncoder.encode("Thapar@123");

        if (userRepository.findByEmailIgnoreCase("student1@thapar.edu").isEmpty()) {
            UserEntity u1 = new UserEntity();
            u1.setId("student_aarav");
            u1.setName("Aarav Sharma");
            u1.setEmail("student1@thapar.edu");
            u1.setPasswordHash(testPasswordHash);
            u1.setHostel("Tejas Hall (Hostel-J)");
            u1.setAvatar("AS");
            u1.setPhone("9876543210");
            u1.setDegree("B.E. Computer Engineering");
            u1.setYear("3rd Year");
            u1.setHometown("Delhi NCR");
            u1.setReliability(5.0);
            u1.setTripsCompleted(4);
            u1.setJoinedCommunity("del");
            userRepository.save(u1);
        }

        if (userRepository.findByEmailIgnoreCase("student2@thapar.edu").isEmpty()) {
            UserEntity u2 = new UserEntity();
            u2.setId("student_diya");
            u2.setName("Diya Verma");
            u2.setEmail("student2@thapar.edu");
            u2.setPasswordHash(testPasswordHash);
            u2.setHostel("Vasudha Hall (Hostel-E)");
            u2.setAvatar("DV");
            u2.setPhone("9876543211");
            u2.setDegree("B.E. Electronics & Communication");
            u2.setYear("2nd Year");
            u2.setHometown("Delhi NCR");
            u2.setReliability(4.9);
            u2.setTripsCompleted(2);
            u2.setJoinedCommunity("del");
            userRepository.save(u2);
        }

        if (userRepository.findByEmailIgnoreCase("test.student1@thapar.edu").isEmpty()) {
            UserEntity u3 = new UserEntity();
            u3.setId("student_aarav_alias");
            u3.setName("Aarav Sharma");
            u3.setEmail("test.student1@thapar.edu");
            u3.setPasswordHash(testPasswordHash);
            u3.setHostel("Tejas Hall (Hostel-J)");
            u3.setAvatar("AS");
            u3.setPhone("9876543210");
            u3.setDegree("B.E. Computer Engineering");
            u3.setYear("3rd Year");
            u3.setHometown("Delhi NCR");
            u3.setReliability(5.0);
            u3.setTripsCompleted(4);
            u3.setJoinedCommunity("del");
            userRepository.save(u3);
        }

        if (userRepository.findByEmailIgnoreCase("test.student2@thapar.edu").isEmpty()) {
            UserEntity u4 = new UserEntity();
            u4.setId("student_diya_alias");
            u4.setName("Diya Verma");
            u4.setEmail("test.student2@thapar.edu");
            u4.setPasswordHash(testPasswordHash);
            u4.setHostel("Vasudha Hall (Hostel-E)");
            u4.setAvatar("DV");
            u4.setPhone("9876543211");
            u4.setDegree("B.E. Electronics & Communication");
            u4.setYear("2nd Year");
            u4.setHometown("Delhi NCR");
            u4.setReliability(4.9);
            u4.setTripsCompleted(2);
            u4.setJoinedCommunity("del");
            userRepository.save(u4);
        }

        if (userRepository.findByEmailIgnoreCase("admin@thapar.edu").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setId("admin_thapar");
            admin.setName("Thapar Admin Desk");
            admin.setEmail("admin@thapar.edu");
            admin.setPasswordHash(testPasswordHash);
            admin.setHostel("Administrative Block");
            admin.setAvatar("AD");
            admin.setPhone("9888999999");
            admin.setDegree("Dean of Student Affairs Office");
            admin.setYear("Administration");
            admin.setHometown("Patiala");
            admin.setReliability(5.0);
            admin.setTripsCompleted(0);
            admin.setRole("admin");
            userRepository.save(admin);
        }

        if (userRepository.findByEmailIgnoreCase("student@thapar.edu").isEmpty()) {
            UserEntity genericStudent = new UserEntity();
            genericStudent.setId("student_default");
            genericStudent.setName("Campus Student");
            genericStudent.setEmail("student@thapar.edu");
            genericStudent.setPasswordHash(testPasswordHash);
            genericStudent.setHostel("Tejas Hall (Hostel-J)");
            genericStudent.setAvatar("CS");
            genericStudent.setPhone("9876543212");
            genericStudent.setDegree("B.E. Computer Engineering");
            genericStudent.setYear("3rd Year");
            genericStudent.setHometown("Delhi NCR");
            genericStudent.setReliability(5.0);
            genericStudent.setTripsCompleted(1);
            genericStudent.setJoinedCommunity("del");
            userRepository.save(genericStudent);
        }
    }
}
