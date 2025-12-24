
import com.name.petmemo.data.models.Checklist

object ChecklistContentEn {
    val checklists: Map<String, List<Checklist>> = mapOf(
        "Cats" to listOf(
            Checklist("Should you get a cat?", listOf(
                "Long-term planning: Are you ready for a 15-20 year commitment?",
                "Finances: Have you calculated monthly costs (food, litter, toys) and a vet budget?",
                "Time: Are you ready to dedicate time for play and social interaction?",
                "Travel: Do you have a plan for who will care for the cat while you are away?",
                "Cleanliness: Are you ready for regular fur cleanup and daily litter box cleaning?",
                "Allergies: Have you checked all family members for allergies?",
                "Home safety: Are \"cat-proof\" nets installed on the windows?",
                "Sterilization: Do you agree to neutering/spaying the cat at 6-8 months old?"
            )),
            Checklist("Cat First Aid Kit", listOf(
                "Thermometer (electronic, for rectal measurement)",
                "Bandaging materials: Bandage, cotton pads, sterile wipes",
                "Antiseptic: Chlorhexidine or Miramistin (NOT alcohol-based!)",
                "Enterosorbents (Enterosgel, Smecta) (ONLY after consulting a doctor)",
                "Blood stopper (Starch, flour, or special powder)",
                "Syringes (without needles) for liquid feeding or administering medication",
                "Nail clippers",
                "Veterinary collar",
                "24/7 Veterinary clinic phone number"
            )),
            Checklist("Packing for a Trip", listOf(
                "Reliable carrier (hard-sided, with good ventilation)",
                "Absorbent pad for the bottom of the carrier",
                "Drinking water and a collapsible bowl",
                "Portion of food",
                "Veterinary passport with vaccinations",
                "Favorite toy or blanket to reduce stress",
                "Leash and harness",
                "Wet wipes and garbage bags"
            )),
            Checklist("Monthly Health Check", listOf(
                "Weight: Checked and recorded",
                "Anti-parasite treatment: Administered according to schedule",
                "Claws: Nail trim performed",
                "Cleaning: Eyes and ears checked and cleaned",
                "Enrichment: Interactive play conducted (hunting simulation)",
                "Shelter: Does the cat have a place where no one disturbs it?"
            ))
        ),
        "Dogs" to listOf(
            Checklist("Should you get a dog?", listOf(
                "Lifestyle: Does your activity level match the breed's needs?",
                "Walks: Are you ready for daily walks in any weather?",
                "Training: Are you ready to spend time and money on a basic obedience course?",
                "Space: Is there enough space for the dog to live comfortably?",
                "Socialization: Are you ready to introduce the puppy to people and animals?",
                "Loneliness: Can the dog stay home alone?",
                "Long-term planning: Do you agree to a 10-15 year commitment?",
                "Care and grooming: Have you calculated grooming expenses?"
            )),
            Checklist("Dog First Aid Kit", listOf(
                "Hydrogen peroxide 3% (for wound rinsing)",
                "Chlorhexidine/Miramistin (antiseptic)",
                "Activated charcoal / Enterosgel (consult a doctor first!)",
                "Blood stopper (or starch/flour)",
                "Elastic bandage and sterile wipes",
                "Thermometer (rectal)",
                "Tweezers / Tick remover hook",
                "Anti-motion sickness tablets",
                "Veterinary passport and 24/7 clinic phone numbers"
            )),
            Checklist("Packing for a Trip (by car)", listOf(
                "Safety belts / Car hammock / Carrier",
                "Water and a collapsible bowl",
                "Portion of familiar food",
                "Equipment set: Collar, harness, leash",
                "Bags for cleanup, wet wipes",
                "Favorite toy or blanket",
                "Tick and flea protection"
            )),
            Checklist("Monthly Health Check", listOf(
                "Vaccination: Next vaccination date checked",
                "Teeth/Mouth: Oral cavity inspected",
                "Paws/Claws: Nail trim performed",
                "Enrichment: Have puzzle toys been used for mental stimulation?",
                "Temperature: Access to water and shade ensured during hot weather"
            ))
        ),
        "Fish" to listOf(
            Checklist("Should you get an aquarium?", listOf(
                "Time: Are you ready to dedicate 30-60 minutes weekly for care?",
                "Patience: Are you ready to wait 3-6 weeks for the 'Nitrogen cycle' to start?",
                "Budget: Have you calculated the cost of equipment and tests?",
                "Size: Can you provide the necessary aquarium volume?",
                "Stability: Can you ensure a stable temperature?",
                "Knowledge: Have you studied fish requirements and compatibility?",
                "Vacation: Is there a plan for who will monitor the aquarium?"
            )),
            Checklist("Aquarium Maintenance (Weekly)", listOf(
                "Water change: Water change performed (20–30%)?",
                "Tests: Water parameters checked (NH3, NO2, NO3)?",
                "Cleaning: Gravel cleaned with a siphon?",
                "Filter: Filter sponges rinsed in aquarium water?",
                "Inspection: Fish inspected for diseases?"
            )),
            Checklist("Aquarium First Aid Kit", listOf(
                "Set of liquid tests (NH3, NO2, NO3)",
                "Water conditioner (dechlorinator)",
                "Ammonia reducer",
                "Spare pump/air pump",
                "Isolation tank / Quarantine aquarium",
                "Aquarium salt",
                "Additional thermometer"
            ))
        ),
        "Small Mammals" to listOf(
            Checklist("Diet (Rabbits and Guinea Pigs)", listOf(
                "Hay: Available 24/7 and makes up 80-90% of the diet",
                "Pellets: Quality pellets used, without grains or muesli",
                "Forbidden: Sweets, bread, grain mixes excluded",
                "Forbidden: White cabbage and legumes excluded",
                "Water: Fresh, clean water is always available",
                "Teeth grinding: Safe wooden toys or branches are available"
            )),
            Checklist("Cage Setup", listOf(
                "Cage: Size matches the species",
                "Floor: Solid floor (no wire mesh floor)",
                "Bedding: Cedar or pine shavings are not used",
                "Bedding: Paper, aspen, fleece, or corn cob used",
                "Temperature: Cage is protected from drafts and direct sunlight",
                "Hamsters: Wheel has the correct diameter (20+ cm)"
            )),
            Checklist("First Aid Kit", listOf(
                "Exotic Vet: Contacts for a specialist rodent veterinarian available",
                "Thermometer (rectal)",
                "Syringes (without needles) for liquid feeding/hydration",
                "Critical Care mix for forced feeding",
                "Antiseptic: Chlorhexidine or Miramistin (NOT alcohol-based!)",
                "Heating pad to warm the animal",
                "Observation: Checking the animal for tumors"
            )),
            Checklist("Socialization", listOf(
                "Pair: Animal (rat/guinea pig/rabbit) is housed in a single-sex pair",
                "Quarantine: New animal has completed 2-3 weeks of quarantine",
                "Neutral territory: First introduction took place on neutral territory",
                "Hamsters: Syrian hamsters are kept strictly alone"
            ))
        ),
        "Birds" to listOf(
            Checklist("Should you get a parrot?", listOf(
                "Long-term planning: Are you ready for a 15-80 year commitment?",
                "Noise: Are you ready for daily loud noise?",
                "Free flight: Are you ready to let the bird out of the cage daily?",
                "Home safety: Are all risks eliminated (Teflon, windows)?",
                "Loneliness: Are you ready to spend enough time with the bird?",
                "Nutrition: Are you ready to provide a proper diet (not just seeds)?",
                "Avian Vet: Is a certified avian veterinarian available in your city?"
            )),
            Checklist("Bird First Aid Kit", listOf(
                "Avian veterinarian phone number",
                "Small, safe carrier",
                "Desk lamp and thermometer (for heating)",
                "Blood stopper (starch/flour)",
                "Tweezers",
                "Syringes (without needles)",
                "Kitchen scale (for weight monitoring)"
            )),
            Checklist("Home Safety", listOf(
                "Teflon: Cookware with non-stick coating excluded",
                "Windows: Nets installed or covered during flights",
                "Fans: Turned off during flights",
                "Cage: Placed in a bright spot, away from drafts",
                "Perches: Natural branches of various diameters are used",
                "Sleep: Complete, dark sleep ensured (10-12 hours)"
            ))
        ),
        "Reptiles and Amphibians" to listOf(
            Checklist("Should you get a reptile?", listOf(
                "Specific care: Are you ready to care for an animal without bonding?",
                "Diet: Are you ready to feed live prey items?",
                "Lighting: Are you ready to provide and regularly replace UV lamps?",
                "Electricity: Are you ready for light and heating costs?",
                "Longevity: Are you ready for a commitment of decades?",
                "Herpetologist: Is a herpetologist veterinarian available in your region?",
                "Hygiene: Are you ready for strict hand hygiene (risk of salmonella)?"
            )),
            Checklist("Terrarium Setup", listOf(
                "Size: Terrarium matches the adult size of the animal",
                "Temperature control: Hot spot and cool zone established",
                "Equipment: Thermometers and hygrometers installed",
                "UV-B lamp: Installed and regularly replaced",
                "Substrate: Safe substrate is used",
                "Light: Correct day and night cycle ensured",
                "Humidity: Necessary humidity level maintained"
            )),
            Checklist("First Aid Kit and Care", listOf(
                "Herpetologist veterinarian phone number",
                "Calcium and vitamin supplements with D3",
                "Spray bottle or fogger",
                "Correct feeder insects/prey ensured",
                "Separate quarantine container available",
                "Separate cleaner used only for the terrarium"
            ))
        )
    )
}