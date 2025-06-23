# Platforma de Învățare Online - Proiect AWBD

Acest proiect reprezintă o aplicație web MVC pentru o platformă de învățare online, dezvoltată în cadrul cursului "Aplicații Web pentru Baze de Date" (AWBD).

## Cuprins

1.  [Descriere Generală](#descriere-generală)
2.  [Tehnologii Utilizate](#tehnologii-utilizate)
3.  [Cerințe Preliminare](#cerințe-preliminare)
4.  [Configurare Mediu de Dezvoltare](#configurare-mediu-de-dezvoltare)
    *   [Configurare Bază de Date MySQL (pentru profilul `prod`)](#configurare-bază-de-date-mysql-pentru-profilul-prod)
5.  [Build și Rulare Proiect](#build-și-rulare-proiect)
    *   [Build](#build)
    *   [Rulare cu profilul default (dev, folosind H2)](#rulare-cu-profilul-default-dev-folosind-h2)
    *   [Rulare cu Profile Specifice](#rulare-cu-profile-specifice)
6.  [Utilizatori de Test (Creați de `DataLoader`)](#utilizatori-de-test-creați-de-dataloader)
7.  [URL-uri Cheie](#url-uri-cheie)
8.  [Structura Proiectului](#structura-proiectului)
9.  [Funcționalități Implementate](#funcționalități-implementate)

## Descriere Generală

Aplicația permite gestionarea cursurilor online, a instructorilor, studenților și modulelor aferente cursurilor. Include funcționalități CRUD complete, autentificare și autorizare bazată pe roluri, paginare, sortare și validarea datelor din formulare.

## Tehnologii Utilizate

*   Java 17+
*   Spring Boot 3.x
*   Spring MVC
*   Spring Data JPA
*   Spring Security
*   Hibernate
*   Thymeleaf
*   Maven
*   H2 Database (pentru profilele `dev` și `test`)
*   MySQL (pentru profilul `prod`)
*   Lombok
*   ModelMapper
*   JUnit 5 & Mockito (pentru testare)

## Cerințe Preliminare

*   JDK 17 sau o versiune mai recentă instalată.
*   Apache Maven 3.6+ instalat.
*   (Opțional, pentru profilul `prod`) Server MySQL instalat și funcțional.
*   Un IDE Java (ex: IntelliJ IDEA, Eclipse).

## Configurare Mediu de Dezvoltare

### Configurare Bază de Date MySQL (pentru profilul `prod`)

Dacă doriți să rulați aplicația cu profilul de producție (`prod`) care utilizează MySQL, urmați acești pași:

1.  **Creați baza de date:**
    Conectați-vă la serverul MySQL și executați următoarea comandă:
    ```sql
    CREATE DATABASE online_learning_prod;
    ```

2.  **Creați un utilizator (sau folosiți unul existent):**
    Exemplu de creare utilizator și acordare privilegii (adaptați `awbd` și `Password@123`):
    ```sql
    CREATE USER 'awbd'@'localhost' IDENTIFIED BY 'passwordplaceholder';
    GRANT ALL PRIVILEGES ON online_learning_prod.* TO 'awbd'@'localhost';
    FLUSH PRIVILEGES;
    ```

3.  **Actualizați fișierul `src/main/resources/application-prod.properties`:**
    Asigurați-vă că detaliile de conexiune (URL, username, password) corespund configurării dvs. MySQL.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/online_learning_prod?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    spring.datasource.username=awbd
    spring.datasource.password=Password@123
    ```
    **Notă:** Parola `Password@123` este un substituent. Înlocuiți-o cu parola reală setată pentru utilizatorul MySQL.

## Build și Rulare Proiect

### Build

Pentru a compila proiectul și a împacheta aplicația (de exemplu, într-un fișier JAR), navigați la directorul rădăcină al proiectului și executați:
```bash
mvn clean install
```

### Rulare cu profilul default (dev, folosind H2)
```bash
mvn spring-boot:run
```

# Rulare cu Profile Specifice
### Rulare cu profilul de producție (MySQL)
```bash
java -jar target/online_learning-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Sau folosind Maven (pentru dezvoltare)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

# Utilizatori de Test (Creați de `DataLoader`)
    admin / adminpass (ROLE_ADMIN, ROLE_STUDENT)

    instructor1 / instrpass (ROLE_INSTRUCTOR, ROLE_STUDENT)

    student1 / studpass (ROLE_STUDENT)

# URL-uri Cheie
    Pagina principală: /

    Login: /login

    Register: /register

    Listă Instructori: /instructors

    Listă Cursuri: /courses

    Listă Studenți: /students (necesită rol ADMIN)

    Consola H2 (doar în profil dev): /h2-console (JDBC URL: jdbc:h2:mem:devdb, User: sa, Pass: [blank])


## Structura Proiectului

Proiectul respectă o arhitectură Model-View-Controller (MVC) și este organizat în următoarele pachete principale:

*   `com.awbd.online_learning` - Pachetul rădăcină al aplicației.
    *   `bootstrap/`: Conține clasa `DataLoader` responsabilă cu popularea inițială a bazei de date cu utilizatori de test și roluri (activă în profilul `dev`).
    *   `config/`: Găzduiește clasele de configurare Spring, cum ar fi `SecurityConfig` pentru Spring Security și `ModelMapperConfig` pentru configurarea ModelMapper.
    *   `controllers/`: Conține controllerele Spring MVC care gestionează cererile HTTP, interacționează cu serviciile și returnează view-urile corespunzătoare. Include și `GlobalExceptionHandler` pentru gestionarea centralizată a excepțiilor.
    *   `domain/`: Definește entitățile JPA (modelele de date) care sunt mapate la tabelele din baza de date. Include entități precum `User`, `Course`, `Instructor`, `Student`, `Module`, etc., și relațiile dintre ele.
    *   `dtos/`: (Data Transfer Objects) Obiecte simple utilizate pentru a transfera date între straturile aplicației, în special între servicii și controllere/view-uri. Acestea ajută la decuplarea modelului de domeniu de interfața cu utilizatorul și pot include validări specifice.
    *   `exceptions/`: Definește clasele de excepții customizate ale aplicației, cum ar fi `ResourceNotFoundException` și `ResourceExistsException`.
    *   `repository/`: Conține interfețele Spring Data JPA Repository care oferă abstracții pentru operațiile de acces la date (CRUD și interogări customizate) pentru fiecare entitate.
    *   `services/`: Conține interfețele și implementările serviciilor. Stratul de servicii încapsulează logica de business a aplicației, coordonând operațiile între controllere și repository-uri. Include și `JpaUserDetailsService` pentru integrarea cu Spring Security.
*   `src/main/resources/`
    *   `application.properties` și `application-{profile}.properties`: Fișiere de configurare pentru diferite medii (dev, test, prod), specificând proprietăți precum sursele de date, configurarea JPA, etc.
    *   `templates/`: Conține template-urile Thymeleaf utilizate pentru a genera paginile HTML dinamice. Organizate în subdirectoare per entitate (ex: `instructor/`, `course/`) și un director `fragments/` pentru elemente reutilizabile (navigație, footer).
    *   `static/`: Conține resursele statice ale aplicației web (CSS).
*   `src/test/java/`: Conține testele unitare și de integrare pentru repository-uri, servicii și controllere.

## Funcționalități Implementate

Aplicația "Platforma de Învățare Online" include următoarele funcționalități cheie, conform cerințelor:

- **Model de Date Complex:**
  1. Definirea a peste 6-7 entități (`User`, `Authority`, `Instructor`, `InstructorProfile`, `Course`, `Module`, `Student`, `Enrollment`).
  2. Implementarea tuturor tipurilor de relații între entități: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`.

- **Operații CRUD Complete:**
  1. Implementarea operațiilor de Creare, Citire, Actualizare și Ștergere (CRUD) pentru entitățile principale (Instructori, Cursuri, Studenți, Module).

-  **Securitate cu Spring Security:**
   1. Autentificare utilizatori (JDBC authentication) folosind username și parolă.
   2. Parolele sunt stocate în baza de date în format hash (BCrypt).
   3. Înregistrarea de noi utilizatori.
   4. Autorizare bazată pe roluri (`ROLE_ADMIN`, `ROLE_INSTRUCTOR`, `ROLE_STUDENT`) pentru a restricționa accesul la diferite funcționalități și URL-uri.
   5. Protecție împotriva atacurilor CSRF (implicit activată de Spring Security).
   6. Pagini customizate pentru login, acces refuzat.

- **Interfață Utilizator (View-uri):**
  1. Utilizarea Thymeleaf pentru randarea paginilor HTML.
  2. Formulare pentru adăugarea și editarea datelor.
  3. Validarea datelor din formulare la nivel de DTO și afișarea mesajelor de eroare.
  4. Utilizarea fragmentelor Thymeleaf pentru elemente comune (meniu de navigație, footer).

- **Gestionarea Excepțiilor:**
  1. Implementarea unui `GlobalExceptionHandler` pentru a trata centralizat excepțiile (ex: resursă negăsită, erori interne) și a afișa pagini de eroare prietenoase.

- **Paginare și Sortare:**
  1. Implementarea paginării și sortării pentru listele de entități (Instructori, Cursuri, Studenți) pentru o navigare eficientă.

- **Profile și Baze de Date Multiple:**
  1. Configurarea și utilizarea profilelor Spring (`dev`, `test`, `prod`).
  2. Utilizarea unei baze de date H2 in-memory pentru dezvoltare (`dev`) și testare (`test`).
  3. Configurarea pentru o bază de date MySQL pentru mediul de producție (`prod`).
  4. Schemă de bază de date creată/actualizată automat de Hibernate (`ddl-auto`).

- **Testare:**
  1. Implementarea de teste unitare și de integrare pentru:
  2. Repository-uri (`@DataJpaTest`).
  3. Servicii (teste unitare cu Mockito).
  4. Controllere (`@WebMvcTest` cu `MockMvc` și `spring-security-test`).

- **Logging:**
  1. Utilizarea SLF4J pentru logging în întreaga aplicație, inclusiv în servicii și gestionarea excepțiilor. Nivelurile de logare sunt configurabile per profil.

- **Populare Inițială a Datelor:**
  1. Un `DataLoader` (activ în profilul `dev`) pentru crearea automată a unor utilizatori de test cu roluri predefinite la pornirea aplicației.