package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Seeds static campus master data (Destinations, Hostels, Communities)
 * into MongoDB at startup if collections are empty.
 * Note: Test student accounts are removed to ensure a clean database.
 */
@Service
public class DatabaseSeederService implements CommandLineRunner {

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Override
    public void run(String... args) {
        seedDestinations();
        seedHostels();
        seedCommunities();
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
            del.setMembersCount(0);

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
}
