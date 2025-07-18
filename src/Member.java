// Interfejs reprezentujący element, który może być odwiedzony przez obiekt implementujący wzorzec Visitor
public interface Member {
    //Akceptuje wizytatora, umożliwiając mu wykonanie operacji na tym obiekcie
    void accept(Visitor visitor);
}
