package Preference;

public abstract class Trade {
    //Это контракты в торговле не считая мизер и расспасовку(они расмотрены как перечисление)
    public static Card sixSpades = new Card(NameCard.шесть, SuitCard.пики, 0);
    public static Card sixClubs = new Card(NameCard.шесть, SuitCard.треф,1);
    public static Card sixDiamonds = new Card(NameCard.шесть, SuitCard.бубн,2);
    public static Card sixHeart = new Card(NameCard.шесть, SuitCard.черви,3);
    public static Card sixBC = new Card(NameCard.шесть, SuitCard.БК,4);

    public static Card sevenSpades = new Card(NameCard.семь, SuitCard.пики, 5);
    public static Card sevenClubs = new Card(NameCard.семь, SuitCard.треф,6);
    public static Card sevenDiamonds = new Card(NameCard.семь, SuitCard.бубн,7);
    public static Card sevenHeart = new Card(NameCard.семь, SuitCard.черви,8);
    public static Card sevenBC = new Card(NameCard.семь, SuitCard.БК,9);

    public static Card eightSpades = new Card(NameCard.восемь, SuitCard.пики, 10);
    public static Card eightClubs = new Card(NameCard.восемь, SuitCard.треф,11);
    public static Card eightDiamonds = new Card(NameCard.восемь, SuitCard.бубн,12);
    public static Card eightHeart = new Card(NameCard.восемь, SuitCard.черви,13);
    public static Card eightBC = new Card(NameCard.восемь, SuitCard.БК,14);

    public static Card nineSpades = new Card(NameCard.девять, SuitCard.пики, 15);
    public static Card nineClubs = new Card(NameCard.девять, SuitCard.треф,16);
    public static Card nineDiamonds = new Card(NameCard.девять, SuitCard.бубн,17);
    public static Card nineHeart = new Card(NameCard.девять, SuitCard.черви,18);
    public static Card nineBC = new Card(NameCard.девять, SuitCard.БК,19);

    public static Card tenSpades = new Card(NameCard.десять, SuitCard.пики, 20);
    public static Card tenClubs = new Card(NameCard.десять, SuitCard.треф,21);
    public static Card tenDiamonds = new Card(NameCard.десять, SuitCard.бубн,22);
    public static Card tenHeart = new Card(NameCard.десять, SuitCard.черви,23);
    public static Card tenBC = new Card(NameCard.десять, SuitCard.БК,24);
}
