package com.example.goodreads.config.init;

import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.model.*;
import com.example.goodreads.repository.*;
import com.example.goodreads.service.user.ShelfService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class DataInitializer {

        @Bean
        @Transactional // important for lazy loading relationships
        public CommandLineRunner loadData(
                        UserRepository userRepository,
                        ShelfService shelfService,
                        ShelfRepository shelfRepository,
                        BookRepository bookRepository,
                        AuthorRepository authorRepository,
                        ReviewRepository reviewRepository,
                        PasswordEncoder passwordEncoder) {
                return args -> {
                        // ==================== USERS ====================
                        // Create 8 test users with different profiles
                        User alice = createUser("alice", "alice@example.com", Role.USER, userRepository,
                                        passwordEncoder);
                        User bob = createUser("bob", "bob@example.com", Role.USER, userRepository, passwordEncoder);
                        User charlie = createUser("charlie", "charlie@example.com", Role.USER, userRepository,
                                        passwordEncoder);
                        User diana = createUser("diana", "diana@example.com", Role.USER, userRepository,
                                        passwordEncoder);
                        User evan = createUser("evan", "evan@example.com", Role.USER, userRepository, passwordEncoder);
                        User fiona = createUser("fiona", "fiona@example.com", Role.USER, userRepository,
                                        passwordEncoder);
                        User george = createUser("george", "george@example.com", Role.ADMIN, userRepository,
                                        passwordEncoder);
                        User hannah = createUser("hannah", "hannah@example.com", Role.USER, userRepository,
                                        passwordEncoder);

                        // One locked user for testing
                        User lockedUser = createUser("locked_user", "locked@example.com", Role.USER, userRepository,
                                        passwordEncoder);
                        lockedUser.setLocked(true);
                        userRepository.save(lockedUser);

                        List<User> activeUsers = List.of(alice, bob, charlie, diana, evan, fiona, george, hannah);

                        // ==================== FETCH BOOKS FIRST ====================
                        // (We need books available before adding to shelves)
                        Book pride = getBook(bookRepository, "Pride and Prejudice");
                        Book gatsby = getBook(bookRepository, "The Great Gatsby");
                        Book mockingbird = getBook(bookRepository, "To Kill a Mockingbird");
                        Book braveNew = getBook(bookRepository, "Brave New World");
                        Book fahrenheit = getBook(bookRepository, "Fahrenheit 451");
                        Book mobyDick = getBook(bookRepository, "Moby Dick");
                        Book catcher = getBook(bookRepository, "The Catcher in the Rye");
                        Book crimeAndPunishment = getBook(bookRepository, "Crime and Punishment");
                        Book warAndPeace = getBook(bookRepository, "War and Peace");
                        Book wuthering = getBook(bookRepository, "Wuthering Heights");

                        Book fellowshipRing = getBook(bookRepository, "The Lord of the Rings: Fellowship of the Ring");
                        Book twoTowers = getBook(bookRepository, "The Lord of the Rings: The Two Towers");
                        Book returnKing = getBook(bookRepository, "The Lord of the Rings: The Return of the King");
                        Book silmarillion = getBook(bookRepository, "The Silmarillion");

                        Book harryPotter1 = getBook(bookRepository, "Harry Potter and the Philosopher's Stone");
                        Book harryPotter2 = getBook(bookRepository, "Harry Potter and the Chamber of Secrets");
                        Book harryPotter3 = getBook(bookRepository, "Harry Potter and the Prisoner of Azkaban");

                        Book hungerGames = getBook(bookRepository, "The Hunger Games");
                        Book catchingFire = getBook(bookRepository, "Catching Fire");
                        Book mockingjay = getBook(bookRepository, "Mockingjay");

                        Book dune = getBook(bookRepository, "Dune");
                        Book nameOfWind = getBook(bookRepository, "The Name of the Wind");
                        Book wiseMansFear = getBook(bookRepository, "The Wise Man's Fear");

                        Book wayOfKings = getBook(bookRepository, "The Way of Kings");
                        Book wordsOfRadiance = getBook(bookRepository, "Words of Radiance");
                        Book mistborn = getBook(bookRepository, "Mistborn: The Final Empire");

                        Book girlDragonTattoo = getBook(bookRepository, "The Girl with the Dragon Tattoo");
                        Book daVinciCode = getBook(bookRepository, "The Da Vinci Code");
                        Book alchemist = getBook(bookRepository, "The Alchemist");
                        Book littlePrince = getBook(bookRepository, "The Little Prince");

                        Book sapiens = getBook(bookRepository, "Sapiens: A Brief History of Humankind");
                        Book atomicHabits = getBook(bookRepository, "Atomic Habits");
                        Book subtleArt = getBook(bookRepository, "The Subtle Art of Not Giving a F*ck");

                        Book martian = getBook(bookRepository, "The Martian");
                        Book projectHailMary = getBook(bookRepository, "Project Hail Mary");
                        Book readyPlayerOne = getBook(bookRepository, "Ready Player One");
                        Book endersGame = getBook(bookRepository, "Ender's Game");

                        Book theRoad = getBook(bookRepository, "The Road");
                        Book theShining = getBook(bookRepository, "The Shining");
                        Book it = getBook(bookRepository, "It");

                        // ==================== DEFAULT SHELVES ====================
                        // Create default shelves (Read, Currently Reading, Want to Read) for all active
                        // users
                        for (User user : activeUsers) {
                                shelfService.createDefaultShelves(user);
                                userRepository.save(user);
                        }

                        // ==================== ADD BOOKS TO DEFAULT SHELVES ====================
                        // Alice - avid reader with books in all default shelves
                        Shelf aliceRead = shelfRepository.findByUserIdAndTypeWithBooks(alice.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf aliceCurrently = shelfRepository
                                        .findByUserIdAndTypeWithBooks(alice.getId(), ShelfType.CURRENTLY_READING)
                                        .orElseThrow();
                        Shelf aliceWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(alice.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        aliceRead.addBook(pride);
                        aliceRead.addBook(mockingbird);
                        aliceRead.addBook(harryPotter1);
                        aliceRead.addBook(harryPotter2);
                        aliceRead.addBook(dune);
                        aliceCurrently.addBook(projectHailMary);
                        aliceCurrently.addBook(wayOfKings);
                        aliceWant.addBook(mistborn);
                        aliceWant.addBook(crimeAndPunishment);
                        aliceWant.addBook(warAndPeace);
                        shelfRepository.save(aliceRead);
                        shelfRepository.save(aliceCurrently);
                        shelfRepository.save(aliceWant);

                        // Bob - fantasy enthusiast
                        Shelf bobRead = shelfRepository.findByUserIdAndTypeWithBooks(bob.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf bobCurrently = shelfRepository
                                        .findByUserIdAndTypeWithBooks(bob.getId(), ShelfType.CURRENTLY_READING)
                                        .orElseThrow();
                        Shelf bobWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(bob.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        bobRead.addBook(fellowshipRing);
                        bobRead.addBook(twoTowers);
                        bobRead.addBook(returnKing);
                        bobRead.addBook(nameOfWind);
                        bobRead.addBook(mistborn);
                        bobCurrently.addBook(wiseMansFear);
                        bobWant.addBook(wordsOfRadiance);
                        bobWant.addBook(silmarillion);
                        shelfRepository.save(bobRead);
                        shelfRepository.save(bobCurrently);
                        shelfRepository.save(bobWant);

                        // Charlie - eclectic reader
                        Shelf charlieRead = shelfRepository
                                        .findByUserIdAndTypeWithBooks(charlie.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf charlieCurrently = shelfRepository
                                        .findByUserIdAndTypeWithBooks(charlie.getId(), ShelfType.CURRENTLY_READING)
                                        .orElseThrow();
                        Shelf charlieWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(charlie.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        charlieRead.addBook(gatsby);
                        charlieRead.addBook(fahrenheit);
                        charlieRead.addBook(theShining);
                        charlieCurrently.addBook(it);
                        charlieWant.addBook(theRoad);
                        charlieWant.addBook(atomicHabits);
                        shelfRepository.save(charlieRead);
                        shelfRepository.save(charlieCurrently);
                        shelfRepository.save(charlieWant);

                        // Diana - classics lover
                        Shelf dianaRead = shelfRepository.findByUserIdAndTypeWithBooks(diana.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf dianaCurrently = shelfRepository
                                        .findByUserIdAndTypeWithBooks(diana.getId(), ShelfType.CURRENTLY_READING)
                                        .orElseThrow();
                        Shelf dianaWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(diana.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        dianaRead.addBook(pride);
                        dianaRead.addBook(wuthering);
                        dianaRead.addBook(littlePrince);
                        dianaRead.addBook(alchemist);
                        dianaCurrently.addBook(crimeAndPunishment);
                        dianaWant.addBook(mobyDick);
                        dianaWant.addBook(warAndPeace);
                        shelfRepository.save(dianaRead);
                        shelfRepository.save(dianaCurrently);
                        shelfRepository.save(dianaWant);

                        // Evan - sci-fi fan
                        Shelf evanRead = shelfRepository.findByUserIdAndTypeWithBooks(evan.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf evanCurrently = shelfRepository
                                        .findByUserIdAndTypeWithBooks(evan.getId(), ShelfType.CURRENTLY_READING)
                                        .orElseThrow();
                        Shelf evanWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(evan.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        evanRead.addBook(dune);
                        evanRead.addBook(martian);
                        evanRead.addBook(endersGame);
                        evanRead.addBook(readyPlayerOne);
                        evanCurrently.addBook(projectHailMary);
                        evanWant.addBook(braveNew);
                        shelfRepository.save(evanRead);
                        shelfRepository.save(evanCurrently);
                        shelfRepository.save(evanWant);

                        // Fiona - balanced reader
                        Shelf fionaRead = shelfRepository.findByUserIdAndTypeWithBooks(fiona.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf fionaWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(fiona.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        fionaRead.addBook(harryPotter1);
                        fionaRead.addBook(hungerGames);
                        fionaRead.addBook(littlePrince);
                        fionaWant.addBook(catchingFire);
                        fionaWant.addBook(girlDragonTattoo);
                        shelfRepository.save(fionaRead);
                        shelfRepository.save(fionaWant);

                        // George (admin) - quick reader
                        Shelf georgeRead = shelfRepository.findByUserIdAndTypeWithBooks(george.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf georgeWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(george.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        georgeRead.addBook(sapiens);
                        georgeRead.addBook(atomicHabits);
                        georgeWant.addBook(subtleArt);
                        shelfRepository.save(georgeRead);
                        shelfRepository.save(georgeWant);

                        // Hannah - thriller enthusiast
                        Shelf hannahRead = shelfRepository.findByUserIdAndTypeWithBooks(hannah.getId(), ShelfType.READ)
                                        .orElseThrow();
                        Shelf hannahCurrently = shelfRepository
                                        .findByUserIdAndTypeWithBooks(hannah.getId(), ShelfType.CURRENTLY_READING)
                                        .orElseThrow();
                        Shelf hannahWant = shelfRepository
                                        .findByUserIdAndTypeWithBooks(hannah.getId(), ShelfType.WANT_TO_READ)
                                        .orElseThrow();
                        hannahRead.addBook(girlDragonTattoo);
                        hannahRead.addBook(daVinciCode);
                        hannahCurrently.addBook(theShining);
                        hannahWant.addBook(it);
                        hannahWant.addBook(theRoad);
                        shelfRepository.save(hannahRead);
                        shelfRepository.save(hannahCurrently);
                        shelfRepository.save(hannahWant);

                        // ==================== CUSTOM SHELVES ====================
                        // Alice's custom shelves
                        Shelf aliceFavorites = new Shelf("Favorites");
                        Shelf aliceSciFi = new Shelf("Sci-Fi Picks");
                        alice.addShelf(aliceFavorites);
                        alice.addShelf(aliceSciFi);
                        shelfRepository.save(aliceFavorites);
                        shelfRepository.save(aliceSciFi);

                        // Bob's custom shelves
                        Shelf bobFantasy = new Shelf("Fantasy Collection");
                        bob.addShelf(bobFantasy);
                        shelfRepository.save(bobFantasy);

                        // Diana's custom shelves
                        Shelf dianaClassics = new Shelf("Classic Literature");
                        Shelf dianaMustRead = new Shelf("Must Read Again");
                        diana.addShelf(dianaClassics);
                        diana.addShelf(dianaMustRead);
                        shelfRepository.save(dianaClassics);
                        shelfRepository.save(dianaMustRead);

                        // ==================== ADD BOOKS TO CUSTOM SHELVES ====================
                        // Alice's shelves
                        aliceFavorites.addBook(pride);
                        aliceFavorites.addBook(mockingbird);
                        aliceFavorites.addBook(littlePrince);
                        aliceSciFi.addBook(dune);
                        aliceSciFi.addBook(martian);
                        aliceSciFi.addBook(endersGame);
                        shelfRepository.save(aliceFavorites);
                        shelfRepository.save(aliceSciFi);

                        // Bob's shelves
                        bobFantasy.addBook(fellowshipRing);
                        bobFantasy.addBook(twoTowers);
                        bobFantasy.addBook(returnKing);
                        bobFantasy.addBook(nameOfWind);
                        bobFantasy.addBook(wayOfKings);
                        shelfRepository.save(bobFantasy);

                        // Diana's shelves
                        dianaClassics.addBook(pride);
                        dianaClassics.addBook(gatsby);
                        dianaClassics.addBook(wuthering);
                        dianaClassics.addBook(crimeAndPunishment);
                        dianaClassics.addBook(warAndPeace);
                        dianaMustRead.addBook(atomicHabits);
                        dianaMustRead.addBook(sapiens);
                        shelfRepository.save(dianaClassics);
                        shelfRepository.save(dianaMustRead);

                        // ==================== REVIEWS ====================
                        // Multiple ratings per book to test average rating calculations

                        // Pride and Prejudice - Classic, well-reviewed
                        reviewRepository.save(new Review(5, "A timeless masterpiece! Austen's wit is unparalleled.",
                                        alice, pride));
                        reviewRepository.save(
                                        new Review(5, "One of the greatest romance novels ever written.", bob, pride));
                        reviewRepository.save(new Review(4, "Loved the character development and social commentary.",
                                        charlie, pride));
                        reviewRepository.save(
                                        new Review(5, "Elizabeth Bennet is such a strong protagonist.", diana, pride));
                        reviewRepository.save(
                                        new Review(4, "The dialogue is so clever and entertaining.", evan, pride));

                        // The Great Gatsby - Mixed reviews
                        reviewRepository.save(new Review(3, "Beautiful prose but the characters are hard to like.",
                                        alice, gatsby));
                        reviewRepository.save(
                                        new Review(4, "A fascinating critique of the American Dream.", bob, gatsby));
                        reviewRepository.save(new Review(5, "Fitzgerald's writing is absolutely mesmerizing.", charlie,
                                        gatsby));
                        reviewRepository.save(
                                        new Review(3, "Interesting but I expected more from the plot.", diana, gatsby));
                        reviewRepository.save(
                                        new Review(4, "The symbolism is rich and thought-provoking.", fiona, gatsby));

                        // To Kill a Mockingbird
                        reviewRepository.save(
                                        new Review(5, "Must-read for everyone. Powerful message.", alice, mockingbird));
                        reviewRepository.save(new Review(5, "Atticus Finch is the moral compass we all need.", bob,
                                        mockingbird));
                        reviewRepository.save(new Review(5, "Scout's perspective makes this so touching.", charlie,
                                        mockingbird));
                        reviewRepository.save(
                                        new Review(4, "Important themes handled beautifully.", diana, mockingbird));

                        // Brave New World
                        reviewRepository.save(
                                        new Review(5, "Terrifyingly relevant to our modern world.", alice, braveNew));
                        reviewRepository.save(
                                        new Review(4, "Makes you think about society and freedom.", bob, braveNew));
                        reviewRepository.save(new Review(4, "Huxley was way ahead of his time.", evan, braveNew));

                        // Fahrenheit 451
                        reviewRepository.save(new Review(5, "Chilling. Bradbury is a genius.", alice, fahrenheit));
                        reviewRepository.save(new Review(5, "A warning we should all heed.", charlie, fahrenheit));
                        reviewRepository.save(new Review(4, "Short but incredibly impactful.", fiona, fahrenheit));

                        // Dune
                        reviewRepository.save(new Review(5, "The greatest sci-fi epic ever written.", bob, dune));
                        reviewRepository.save(new Review(5, "World-building is absolutely incredible.", alice, dune));
                        reviewRepository.save(
                                        new Review(4, "Complex but rewarding. Keep a glossary handy!", charlie, dune));
                        reviewRepository.save(new Review(5, "Herbert created something truly unique.", evan, dune));
                        reviewRepository.save(new Review(4, "Politics, religion, ecology - all woven brilliantly.",
                                        hannah, dune));

                        // LOTR Fellowship
                        reviewRepository.save(new Review(5, "The beginning of the greatest fantasy journey.", bob,
                                        fellowshipRing));
                        reviewRepository.save(
                                        new Review(5, "Tolkien's prose is like poetry.", charlie, fellowshipRing));
                        reviewRepository.save(
                                        new Review(5, "Changed my life when I was a teenager.", diana, fellowshipRing));
                        reviewRepository.save(
                                        new Review(4, "Starts slow but absolutely worth it.", evan, fellowshipRing));

                        // Harry Potter 1
                        reviewRepository.save(
                                        new Review(5, "Magical! Started my love for reading.", alice, harryPotter1));
                        reviewRepository.save(new Review(5, "Perfect introduction to the wizarding world.", bob,
                                        harryPotter1));
                        reviewRepository.save(new Review(4, "Great for all ages.", charlie, harryPotter1));
                        reviewRepository.save(new Review(5, "Nostalgia in book form.", diana, harryPotter1));
                        reviewRepository.save(
                                        new Review(5, "J.K. Rowling created something special.", fiona, harryPotter1));
                        reviewRepository.save(new Review(4, "Fun, whimsical, and heartwarming.", george, harryPotter1));

                        // The Martian
                        reviewRepository.save(
                                        new Review(5, "Science fiction at its finest. So funny too!", alice, martian));
                        reviewRepository.save(new Review(5, "Couldn't put it down. Weir is brilliant.", bob, martian));
                        reviewRepository.save(new Review(4, "Love the problem-solving approach.", charlie, martian));
                        reviewRepository.save(new Review(5, "Mark Watney is the best narrator ever.", evan, martian));

                        // Atomic Habits
                        reviewRepository.save(
                                        new Review(5, "Life-changing. Applied it immediately.", alice, atomicHabits));
                        reviewRepository.save(new Review(5, "Practical and actionable advice.", bob, atomicHabits));
                        reviewRepository.save(
                                        new Review(4, "Great concepts, could be more concise.", charlie, atomicHabits));
                        reviewRepository.save(new Review(5, "Everyone should read this.", diana, atomicHabits));
                        reviewRepository.save(new Review(4, "Helped me build better routines.", fiona, atomicHabits));

                        // The Shining
                        reviewRepository.save(new Review(5, "Stephen King at his absolute best. Terrifying.", bob,
                                        theShining));
                        reviewRepository.save(
                                        new Review(4, "Couldn't sleep for days after reading.", charlie, theShining));
                        reviewRepository.save(
                                        new Review(5, "The psychological horror is masterful.", evan, theShining));
                        reviewRepository.save(
                                        new Review(4, "Better than the movie in so many ways.", fiona, theShining));

                        // Crime and Punishment
                        reviewRepository.save(new Review(5, "Dostoevsky understands the human psyche like no one else.",
                                        diana, crimeAndPunishment));
                        reviewRepository.save(new Review(4, "Heavy but rewarding read.", charlie, crimeAndPunishment));
                        reviewRepository.save(new Review(5, "Philosophy wrapped in a gripping narrative.", george,
                                        crimeAndPunishment));

                        // The Little Prince
                        reviewRepository.save(
                                        new Review(5, "Simple yet profound. Cried at the end.", alice, littlePrince));
                        reviewRepository.save(
                                        new Review(5, "Adults need this more than children.", diana, littlePrince));
                        reviewRepository.save(new Review(5, "What is essential is invisible to the eye.", fiona,
                                        littlePrince));
                        reviewRepository.save(
                                        new Review(4, "Beautiful illustrations and message.", hannah, littlePrince));

                        // The Alchemist
                        reviewRepository.save(new Review(4, "Inspiring story about following your dreams.", alice,
                                        alchemist));
                        reviewRepository.save(new Review(3, "Good message but a bit simplistic.", bob, alchemist));
                        reviewRepository.save(new Review(5, "Changed my perspective on life.", charlie, alchemist));
                        reviewRepository.save(new Review(4, "Quick read with lasting impact.", diana, alchemist));

                        // Ender's Game
                        reviewRepository.save(new Review(5, "Mind-blowing ending. Did not see it coming.", alice,
                                        endersGame));
                        reviewRepository.save(new Review(5, "One of the best sci-fi novels period.", bob, endersGame));
                        reviewRepository.save(new Review(4, "Ender is such a complex character.", charlie, endersGame));
                        reviewRepository.save(
                                        new Review(5, "Military strategy meets coming of age.", evan, endersGame));

                        // Name of the Wind
                        reviewRepository.save(
                                        new Review(5, "Rothfuss's prose is absolutely beautiful.", bob, nameOfWind));
                        reviewRepository.save(new Review(5, "Kvothe is one of the best fantasy protagonists.", charlie,
                                        nameOfWind));
                        reviewRepository.save(new Review(4, "Just wish book 3 would come out!", diana, nameOfWind));
                        reviewRepository.save(new Review(5, "Magic system is creative and well-explained.", evan,
                                        nameOfWind));

                        // Way of Kings
                        reviewRepository.save(
                                        new Review(5, "Epic in every sense. Sanderson is a master.", bob, wayOfKings));
                        reviewRepository.save(new Review(5, "The world-building is incredible.", charlie, wayOfKings));
                        reviewRepository.save(new Review(4, "Long but every page is worth it.", evan, wayOfKings));

                        // Ready Player One
                        reviewRepository.save(new Review(4, "Nostalgia overload! So many references.", alice,
                                        readyPlayerOne));
                        reviewRepository.save(
                                        new Review(4, "Fun adventure if you like 80s culture.", bob, readyPlayerOne));
                        reviewRepository.save(new Review(3, "Entertaining but not deep.", charlie, readyPlayerOne));
                        reviewRepository.save(new Review(4, "Perfect escapism.", evan, readyPlayerOne));

                        // Sapiens
                        reviewRepository.save(new Review(5, "Fascinating look at human history.", alice, sapiens));
                        reviewRepository.save(
                                        new Review(5, "Made me think about humanity differently.", diana, sapiens));
                        reviewRepository.save(new Review(4, "Some claims are debatable but thought-provoking.", george,
                                        sapiens));

                        // The Road
                        reviewRepository.save(new Review(5, "Devastatingly beautiful. McCarthy is a poet.", charlie,
                                        theRoad));
                        reviewRepository.save(new Review(4, "Bleak but powerful.", diana, theRoad));
                        reviewRepository.save(
                                        new Review(5, "The father-son relationship is heartbreaking.", evan, theRoad));

                        // Moby Dick
                        reviewRepository.save(new Review(4, "An epic tale of obsession. Dense but rewarding.", charlie,
                                        mobyDick));
                        reviewRepository.save(new Review(3, "Classic but quite slow in parts.", bob, mobyDick));
                        reviewRepository.save(new Review(5, "Melville's prose is unmatched.", george, mobyDick));

                        // The Catcher in the Rye
                        reviewRepository.save(
                                        new Review(5, "Holden Caulfield speaks to every teenager.", alice, catcher));
                        reviewRepository.save(new Review(3, "Important but Holden can be annoying.", bob, catcher));
                        reviewRepository.save(new Review(4, "A snapshot of adolescent angst.", diana, catcher));

                        // The Silmarillion
                        reviewRepository.save(new Review(5, "For true Tolkien fans. The mythology is incredible.", bob,
                                        silmarillion));
                        reviewRepository.save(
                                        new Review(4, "Dense but beautiful world-building.", charlie, silmarillion));
                        reviewRepository.save(new Review(3, "Reads more like a history book than a novel.", evan,
                                        silmarillion));

                        // Harry Potter 2 & 3
                        reviewRepository.save(new Review(5, "The mystery element makes this one great.", alice,
                                        harryPotter2));
                        reviewRepository.save(
                                        new Review(4, "Darker than the first, in a good way.", bob, harryPotter2));
                        reviewRepository.save(new Review(5, "Best of the series! Time travel done right.", alice,
                                        harryPotter3));
                        reviewRepository.save(new Review(5, "Sirius Black is such a great character.", charlie,
                                        harryPotter3));
                        reviewRepository.save(new Review(5, "The Patronus scene gives me chills every time.", fiona,
                                        harryPotter3));

                        // Hunger Games trilogy
                        reviewRepository.save(new Review(5, "Gripping from start to finish.", alice, hungerGames));
                        reviewRepository.save(new Review(5, "Katniss is an amazing protagonist.", bob, hungerGames));
                        reviewRepository.save(new Review(4, "Great social commentary.", charlie, hungerGames));
                        reviewRepository.save(new Review(5, "Even better than the first!", alice, catchingFire));
                        reviewRepository.save(new Review(4, "The stakes get so much higher.", diana, catchingFire));
                        reviewRepository.save(new Review(4, "Intense conclusion. War is shown realistically.", alice,
                                        mockingjay));
                        reviewRepository.save(
                                        new Review(3, "Darker ending than expected but fitting.", bob, mockingjay));

                        // Wise Man's Fear
                        reviewRepository.save(new Review(5, "Even better than Name of the Wind.", bob, wiseMansFear));
                        reviewRepository.save(new Review(4, "Some sections drag but mostly fantastic.", charlie,
                                        wiseMansFear));
                        reviewRepository.save(new Review(5, "Rothfuss is a master of prose.", evan, wiseMansFear));

                        // Words of Radiance & Mistborn
                        reviewRepository.save(new Review(5, "Somehow better than Way of Kings!", bob, wordsOfRadiance));
                        reviewRepository.save(new Review(5, "Shallan's story really develops here.", charlie,
                                        wordsOfRadiance));
                        reviewRepository.save(new Review(5, "One of the best fantasy books I've read.", bob, mistborn));
                        reviewRepository.save(new Review(5, "The magic system is so creative.", alice, mistborn));
                        reviewRepository.save(new Review(4, "Great heist story in a fantasy setting.", evan, mistborn));

                        // Thrillers
                        reviewRepository.save(new Review(5, "Couldn't put it down! Lisbeth is unforgettable.", alice,
                                        girlDragonTattoo));
                        reviewRepository.save(new Review(4, "Gripping Swedish noir.", charlie, girlDragonTattoo));
                        reviewRepository.save(
                                        new Review(4, "Fast-paced thriller with great twists.", bob, daVinciCode));
                        reviewRepository.save(new Review(3, "Entertaining but historically questionable.", diana,
                                        daVinciCode));

                        // Self-help
                        reviewRepository.save(new Review(4, "Refreshing take on self-improvement.", bob, subtleArt));
                        reviewRepository.save(new Review(5, "Finally, practical life advice!", charlie, subtleArt));
                        reviewRepository.save(new Review(3, "Good points but the tone can be off-putting.", diana,
                                        subtleArt));

                        // More Sci-Fi
                        reviewRepository.save(new Review(5, "Even better than The Martian!", alice, projectHailMary));
                        reviewRepository.save(new Review(5, "Rocky is the best character ever.", bob, projectHailMary));
                        reviewRepository.save(new Review(5, "Weir outdid himself. Incredible.", evan, projectHailMary));
                        reviewRepository.save(new Review(4, "The science is fascinating.", hannah, projectHailMary));

                        // More Horror
                        reviewRepository.save(new Review(5, "Genuinely terrifying. King's masterpiece.", bob, it));
                        reviewRepository.save(new Review(4, "Long but absolutely worth it.", charlie, it));
                        reviewRepository.save(new Review(5, "Pennywise will haunt your dreams.", evan, it));

                        System.out.println("🌱 Comprehensive test data loaded successfully!");
                        System.out.println("   - 9 users created (8 active, 1 locked, 1 admin)");
                        System.out.println("   - Default shelves with books for all active users (~45 shelf entries)");
                        System.out.println("   - 5 custom shelves created with books");
                        System.out.println("   - 100+ reviews across 35+ books");
                };
        }

        private User createUser(String username, String email, Role role,
                        UserRepository userRepository, PasswordEncoder passwordEncoder) {
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode("password"));
                user.setRole(role);
                return userRepository.save(user);
        }

        private Book getBook(BookRepository bookRepository, String title) {
                return bookRepository.findByTitleIgnoreCaseWithAuthor(title)
                                .orElseThrow(() -> new BookNotFoundException("Book not found: " + title));
        }
}
