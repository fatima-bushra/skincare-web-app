package com.glowmatch.service;

import com.glowmatch.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final List<QuizQuestion> questions;
    private final List<Product> allProducts;

    public QuizService() {
        this.questions = initQuestions();
        this.allProducts = initProducts();
    }

    public List<QuizQuestion> getAllQuestions() {
        return questions;
    }

    public QuizResult processAnswers(QuizSubmission submission) {
        Map<Integer, String> answers = submission.getAnswers();
        List<String> profileTags = buildProfileTags(answers);
        List<Product> matched = matchProducts(answers, profileTags);
        return new QuizResult(profileTags, matched);
    }

    private List<String> buildProfileTags(Map<Integer, String> answers) {
        List<String> tags = new ArrayList<>();

        // Q1: skin type
        String q1 = answers.get(1);
        if ("oily".equals(q1)) tags.add("Oily Skin");
        else if ("dry".equals(q1)) tags.add("Dry Skin");
        else if ("combination".equals(q1)) tags.add("Combination Skin");
        else if ("sensitive".equals(q1)) tags.add("Sensitive Skin");

        // Q2: concerns
        String q2 = answers.get(2);
        if ("acne".equals(q2)) tags.add("Acne-Prone");
        else if ("aging".equals(q2)) tags.add("Anti-Aging");
        else if ("dullness".equals(q2)) tags.add("Brightening");
        else if ("hydration".equals(q2)) tags.add("Dehydrated");

        // Q3: brand preference
        String q3 = answers.get(3);
        if ("korean".equals(q3)) tags.add("Korean Brands");
        else if ("western".equals(q3)) tags.add("Western Brands");
        else if ("natural".equals(q3)) tags.add("Natural/Organic");
        else tags.add("All Brands");

        // Q4: budget
        String q4 = answers.get(4);
        if ("budget".equals(q4)) tags.add("Budget-Friendly");
        else if ("mid".equals(q4)) tags.add("Mid-Range");
        else if ("luxury".equals(q4)) tags.add("Luxury");

        // Q5: routine complexity
        String q5 = answers.get(5);
        if ("minimal".equals(q5)) tags.add("Minimal Routine");
        else if ("full".equals(q5)) tags.add("Full Routine");

        // Q6: climate
        String q6 = answers.get(6);
        if ("humid".equals(q6)) tags.add("Humid Climate");
        else if ("dry".equals(q6)) tags.add("Dry Climate");

        // Q7: age range
        String q7 = answers.get(7);
        if (q7 != null) tags.add("Age " + q7);

        // Q8: fragrance preference
        String q8 = answers.get(8);
        if ("fragrance_free".equals(q8)) tags.add("Fragrance-Free");
        else if ("light".equals(q8)) tags.add("Light Fragrance");

        return tags;
    }

    private List<Product> matchProducts(Map<Integer, String> answers, List<String> profileTags) {
        String skinType = answers.getOrDefault(1, "combination");
        String concern = answers.getOrDefault(2, "hydration");
        String brandPref = answers.getOrDefault(3, "any");
        String budget = answers.getOrDefault(4, "mid");

        return allProducts.stream()
                .filter(p -> matchesProfile(p, skinType, concern, brandPref, budget))
                .sorted(Comparator.comparingInt(p -> scoreProduct((Product) p, skinType, concern)).reversed())
                .limit(9)
                .collect(Collectors.toList());
    }

    private boolean matchesProfile(Product p, String skinType, String concern, String brandPref, String budget) {
        // At least one matching tag
        List<String> mt = p.getMatchTags();
        boolean skinMatch = mt.contains(skinType) || mt.contains("all");
        boolean budgetMatch = "any".equals(budget) || mt.contains(budget) || mt.contains("all");
        boolean brandMatch = "any".equals(brandPref) || mt.contains(brandPref) || mt.contains("all");
        return skinMatch && budgetMatch && brandMatch;
    }

    private int scoreProduct(Product p, String skinType, String concern) {
        int score = 0;
        List<String> mt = p.getMatchTags();
        if (mt.contains(skinType)) score += 3;
        if (mt.contains(concern)) score += 3;
        if (mt.contains("all")) score += 1;
        return score;
    }

    private List<QuizQuestion> initQuestions() {
        List<QuizQuestion> qs = new ArrayList<>();

        qs.add(new QuizQuestion(1, "SKIN TYPE",
                "How does your skin usually feel by midday?",
                List.of(
                        new QuizOption("oily", "🫧", "Oily", "oily"),
                        new QuizOption("dry", "🏜️", "Dry", "dry"),
                        new QuizOption("combination", "🌗", "Combination", "combination"),
                        new QuizOption("sensitive", "🌸", "Sensitive", "sensitive")
                )));

        qs.add(new QuizQuestion(2, "SKIN CONCERN",
                "What's your biggest skin concern right now?",
                List.of(
                        new QuizOption("acne", "🔴", "Acne & Breakouts", "acne"),
                        new QuizOption("aging", "⏳", "Fine Lines & Aging", "aging"),
                        new QuizOption("dullness", "✨", "Dullness & Uneven Tone", "dullness"),
                        new QuizOption("hydration", "💧", "Dehydration & Dryness", "hydration")
                )));

        qs.add(new QuizQuestion(3, "BRAND PREFERENCE",
                "Which type of skincare brands do you prefer?",
                List.of(
                        new QuizOption("korean", "🇰🇷", "Korean Beauty", "korean"),
                        new QuizOption("western", "🌎", "Western Brands", "western"),
                        new QuizOption("natural", "🌿", "Natural & Organic", "natural"),
                        new QuizOption("any", "🌈", "No Preference", "any")
                )));

        qs.add(new QuizQuestion(4, "BUDGET",
                "What's your typical budget per skincare product?",
                List.of(
                        new QuizOption("budget", "💚", "Under $20", "budget"),
                        new QuizOption("mid", "💛", "$20 – $50", "mid"),
                        new QuizOption("luxury", "💎", "$50+", "luxury"),
                        new QuizOption("any", "🤷", "No Preference", "any")
                )));

        qs.add(new QuizQuestion(5, "ROUTINE",
                "How complex is your ideal skincare routine?",
                List.of(
                        new QuizOption("minimal", "⚡", "Minimal (2–3 steps)", "minimal"),
                        new QuizOption("moderate", "🌙", "Moderate (4–6 steps)", "moderate"),
                        new QuizOption("full", "🌟", "Full Routine (7+ steps)", "full"),
                        new QuizOption("unsure", "🤔", "Still Figuring It Out", "any")
                )));

        qs.add(new QuizQuestion(6, "CLIMATE",
                "What's the climate like where you live?",
                List.of(
                        new QuizOption("humid", "🌴", "Hot & Humid", "humid"),
                        new QuizOption("dry", "☀️", "Hot & Dry", "dry"),
                        new QuizOption("cold", "❄️", "Cold & Dry", "cold"),
                        new QuizOption("mixed", "🌤️", "Mixed / Seasonal", "any")
                )));

        qs.add(new QuizQuestion(7, "AGE RANGE",
                "Which age range are you in?",
                List.of(
                        new QuizOption("teens", "🌱", "Under 20", "teens"),
                        new QuizOption("twenties", "🌸", "20s", "twenties"),
                        new QuizOption("thirties", "🌺", "30s", "thirties"),
                        new QuizOption("40plus", "🌹", "40s & above", "40plus")
                )));

        qs.add(new QuizQuestion(8, "FRAGRANCE",
                "How do you feel about fragrance in skincare?",
                List.of(
                        new QuizOption("fragrance_free", "🚫", "Fragrance-Free Only", "fragrance_free"),
                        new QuizOption("light", "🌸", "Light Fragrance is Fine", "light"),
                        new QuizOption("love", "🌷", "Love Scented Products", "love"),
                        new QuizOption("no_pref", "😊", "No Preference", "any")
                )));

        return qs;
    }

    private List<Product> initProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product("p1", "COSRX", "Advanced Snail 96 Mucin Power Essence",
                "Serum", "Deeply hydrates and repairs the skin barrier — perfect for dry or sensitive types.",
                "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "BUDGET"), List.of("dry", "sensitive", "hydration", "korean", "budget", "all"), 14.0));

        products.add(new Product("p2", "Innisfree", "Green Tea Hyaluronic Moisturizer",
                "Moisturizer", "Lightweight gel-cream locks in moisture without clogging pores.",
                "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "MID-RANGE"), List.of("oily", "combination", "hydration", "korean", "mid", "all"), 34.0));

        products.add(new Product("p3", "Some By Mi", "AHA·BHA·PHA 30 Days Miracle Toner",
                "Toner", "Gently exfoliates and clears breakouts — ideal for acne-prone and oily skin.",
                "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "BUDGET"), List.of("oily", "acne", "korean", "budget", "all"), 18.0));

        products.add(new Product("p4", "MISSHA", "Time Revolution First Treatment Essence",
                "Serum", "Fermented yeast essence brightens dullness and improves texture over time.",
                "https://images.unsplash.com/photo-1617897903246-719242758050?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "MID-RANGE"), List.of("dullness", "aging", "korean", "mid", "all"), 44.0));

        products.add(new Product("p5", "CeraVe", "Hydrating Facial Cleanser",
                "Cleanser", "Ceramide-rich formula cleanses without stripping — perfect for dry or sensitive skin.",
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&q=80",
                "https://amazon.com", List.of("WESTERN", "BUDGET"), List.of("dry", "sensitive", "western", "budget", "all"), 15.0));

        products.add(new Product("p6", "The Ordinary", "Niacinamide 10% + Zinc 1%",
                "Serum", "Controls sebum production and minimizes the appearance of pores and blemishes.",
                "https://images.unsplash.com/photo-1512290923902-8a9f81dc236c?w=400&q=80",
                "https://amazon.com", List.of("WESTERN", "BUDGET"), List.of("oily", "acne", "dullness", "western", "budget", "all"), 11.0));

        products.add(new Product("p7", "Sulwhasoo", "First Care Activating Serum",
                "Serum", "Luxury Korean herbal serum that boosts absorption of following skincare steps.",
                "https://images.unsplash.com/photo-1631729371254-42c2892f0e6e?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "LUXURY"), List.of("aging", "dullness", "korean", "luxury", "all"), 92.0));

        products.add(new Product("p8", "Purito", "Daily Go-To Sunscreen SPF50",
                "Sunscreen", "Lightweight, non-greasy broad spectrum SPF — no white cast for all skin tones.",
                "https://images.unsplash.com/photo-1556228453-efd6c1ff04f6?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "BUDGET"), List.of("oily", "combination", "sensitive", "korean", "budget", "all"), 16.0));

        products.add(new Product("p9", "Tatcha", "The Water Cream",
                "Moisturizer", "Oil-free, anti-aging water cream packed with Japanese botanicals and silk extracts.",
                "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=400&q=80",
                "https://amazon.com", List.of("LUXURY", "JAPANESE"), List.of("oily", "aging", "dullness", "luxury", "all"), 68.0));

        products.add(new Product("p10", "TONYMOLY", "I'm Real Bamboo Sheet Mask",
                "Moisturizer", "Soothing bamboo extract mask that calms redness and deeply hydrates sensitive skin.",
                "https://images.unsplash.com/photo-1570194065650-d99fb4bedf0a?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "BUDGET"), List.of("sensitive", "hydration", "korean", "budget", "all"), 4.0));

        products.add(new Product("p11", "Klairs", "Midnight Blue Calming Cream",
                "Moisturizer", "Guaiazulene formula reduces inflammation and soothes sensitive or reactive skin overnight.",
                "https://images.unsplash.com/photo-1608248543803-ba4f8c70ae0b?w=400&q=80",
                "https://amazon.com", List.of("KOREAN", "MID-RANGE"), List.of("sensitive", "acne", "korean", "mid", "all"), 27.0));

        products.add(new Product("p12", "La Roche-Posay", "Effaclar Duo Acne Treatment",
                "Serum", "Dual-action formula with benzoyl peroxide and lipo-hydroxy acid to clear stubborn acne.",
                "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400&q=80",
                "https://amazon.com", List.of("WESTERN", "MID-RANGE"), List.of("oily", "acne", "western", "mid", "all"), 30.0));

        return products;
    }
}
