
/*
 * Paaristöö. 1
 * Rait Herman
 * Jaan Kaasik
 * */

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Gomoku extends Application {
	// muutuja seadmaks mänguvälja suurust
	public static int mõõtmed = 18;
	private Mängija punaneMängija = new Mängija(Color.RED, "Punane");
	private Mängija sinineMängija = new Mängija(Color.BLUE, "Sinine");
	private Mängija aktiivneMängija = punaneMängija;
	private Label teavitus = new Label();
	private BorderPane välineAken = new BorderPane();
	private GridPane mänguväli = new GridPane();
	private VBox vbUusMäng = new VBox();
	private Map<Koordinaadid, Lahter> väljad = new HashMap<Koordinaadid, Lahter>();
	private double akenKõrgus = 675;
	private double akenLaius = 600;
	private FileWriter fw = null;
	private String failTee = "käigud.txt"; // siia salvestame programmi käimise jooksul tehtud käigud
	private List<Koordinaadid> keelatudKoordinaadid = new ArrayList<>();

	public static void main(String[] args) {
		launch(args);
	}

	// loome uue mänguvälja ja nullime mängijad
	public void algSeis(int mõõtmed, int mänguTüüp, Mängija aktiivneMängija) {
		mänguväli.getChildren().clear();
		Gomoku.mõõtmed = mõõtmed;
		for (int i = 0; i < mõõtmed; i++) {
			for (int j = 0; j < mõõtmed; j++) {
				Lahter lahter = new Lahter(new Koordinaadid(i, j));
				mänguväli.add(lahter, j, i);
				// lisame kõik väljad ka listi, kust saame kontrollida kas väli on täis või
				// mitte
				väljad.put(lahter.getKoordinaadid(), lahter);
			}
		}
		punaneMängija.algSeis(false);
		sinineMängija.algSeis(mänguTüüp == 1);
		this.aktiivneMängija = aktiivneMängija;
		teavitus.setText(aktiivneMängija + " alustab");
		välineAken.setBottom(annaAlumineRiba());
	}

	// loome enamuse mängu jooksul kasutatavast sisust/graafikast jne
	@Override
	public void start(Stage stage) throws Exception {
		Files.deleteIfExists(Paths.get(failTee));
		stage.setTitle("Gomoku");
		joonistaAlgVaade();
		välineAken.setTop(annaMenüü());
		välineAken.setCenter(mänguväli);
		teeUueMänguLoomiseVaade();
		stage.setScene(new Scene(välineAken, akenLaius, akenKõrgus));
		stage.getIcons().add(new Image("file:src/resources/icon.png"));
		stage.setResizable(false);
		stage.show();
	}

	// avakuva
	private void joonistaAlgVaade() {
		// alustamise nupp
		ImageView nupp = annaPilt("resources/button_0.png");
		nupp.setOnMouseClicked(e -> looUusMäng());
		nupp.setCursor(Cursor.HAND);
		mänguväli.getChildren().addAll(annaPilt("resources/gomoku_banner.png", 0.0, akenKõrgus, false),
				new StackPane(nupp));
	}

	// siin loome kõik menüüd ja valikud, et saaks määrata mängu tüüpi jne.
	private void teeUueMänguLoomiseVaade() {
		Insets padding10 = new Insets(10);
		Label lblUusMäng = new Label("Uus mäng:");
		lblUusMäng.setFont(new Font(20));
		lblUusMäng.setPadding(padding10);

		// Mänguvälja suuruse määramine
		Spinner<Integer> suurus = new Spinner<>(8, 40, 18);
		HBox hbVäljaSuurus = new HBox(new Label("Välja suurus: "), suurus);
		hbVäljaSuurus.setAlignment(Pos.CENTER);
		hbVäljaSuurus.setPadding(padding10);

		// Mängu tüübi valimine
		HBox hbMänguTüüp = new HBox();
		hbMänguTüüp.setPadding(padding10);
		hbMänguTüüp.setAlignment(Pos.CENTER);
		ToggleGroup tgMänguTüüp = new ToggleGroup();
		RadioButton inimeneVsArvuti = new RadioButton("Inimene vs Arvuti");
		RadioButton inimeneVsInimene = new RadioButton("Inimene vs Inimene");
		inimeneVsInimene.setSelected(true);
		inimeneVsArvuti.setToggleGroup(tgMänguTüüp);
		inimeneVsInimene.setToggleGroup(tgMänguTüüp);
		hbMänguTüüp.getChildren().addAll(new Label("Mängu tüüp: "), inimeneVsArvuti, inimeneVsInimene);

		// Alustava värvi valimine
		HBox hbAlustaja = new HBox();
		hbAlustaja.setPadding(padding10);
		hbAlustaja.setAlignment(Pos.CENTER);

		ToggleGroup tgAlustaja = new ToggleGroup();
		RadioButton alustabPunane = new RadioButton("Punane");
		alustabPunane.setToggleGroup(tgAlustaja);
		alustabPunane.setSelected(true);
		RadioButton alustabSinine = new RadioButton("Sinine");
		alustabSinine.setToggleGroup(tgAlustaja);
		hbAlustaja.getChildren().addAll(new Label("Alustaja: "), alustabPunane, alustabSinine);

		Button alustaMängu = new Button("Alusta mängu");
		alustaMängu.setAlignment(Pos.CENTER);
		alustaMängu.setOnAction(e -> algSeis(suurus.getValue(), inimeneVsArvuti.isSelected() ? 1 : -1,
				alustabPunane.isSelected() ? punaneMängija : sinineMängija));
		vbUusMäng.setAlignment(Pos.CENTER);
		vbUusMäng.setMinSize(akenLaius, akenKõrgus);
		vbUusMäng.setMaxSize(akenLaius, akenKõrgus);
		vbUusMäng.getChildren().addAll(lblUusMäng, hbVäljaSuurus, hbMänguTüüp, hbAlustaja, alustaMängu);
	}

	// alumisel real hoiame mängu seisu ning tagasivõtmise nuppu
	private HBox annaAlumineRiba() {
		// loome külgriba
		HBox alariba = new HBox();
		VBox tulemused = new VBox();
		alariba.setAlignment(Pos.CENTER);
		tulemused.setAlignment(Pos.CENTER);
		tulemused.setMinHeight(75);
		tulemused.getChildren().addAll(punaneMängija.getTulemusLabel(), sinineMängija.getTulemusLabel(), teavitus);
		ImageView undoNupp = annaPilt("resources/undo.png", 30, 30, true);
		undoNupp.setCursor(Cursor.HAND);
		undoNupp.setOnMouseClicked(e -> võtaKäikTagasi());
		alariba.getChildren().addAll(undoNupp, tulemused);
		return alariba;
	}

	// meetod tühistab viimase käigu
	private void võtaKäikTagasi() {
		Path path = Paths.get(failTee);
		try {
			String failiSisu = new String(Files.readAllBytes(path));
			String[] sisu = failiSisu.split(";");
			String koordinaadidSisu = sisu[sisu.length - 1];
			String[] koordinaadid = sisu[sisu.length - 1].split("/");
			if (failiSisu.trim().equals("")) {
				return;
			}
			int y = Integer.valueOf(koordinaadid[0]);
			int x = Integer.valueOf(koordinaadid[1]);
			System.out.println(aktiivneMängija.getKäigud()[x][y]);
			väljad.get(new Koordinaadid(y, x)).tyhjenda();
			vahetaAktiivneMängija();
			aktiivneMängija.getKäigud()[y][x] = 0;
			failiSisu = failiSisu.replaceAll(koordinaadidSisu + ";", "");
			Files.write(path, failiSisu.getBytes());
		} catch (IOException e) {
		}
	}

	private void vahetaAktiivneMängija() {
		aktiivneMängija = aktiivneMängija.equals(punaneMängija) ? sinineMängija : punaneMängija;
	}

	private MenuBar annaMenüü() {
		MenuBar menüü = new MenuBar();
		Menu menüüFail = new Menu("Fail");
		MenuItem uusMäng = new MenuItem("Uus mäng");
		MenuItem välju = new MenuItem("Välju");
		uusMäng.setOnAction(e -> looUusMäng());
		välju.setOnAction(e -> System.exit(1));
		menüüFail.getItems().addAll(uusMäng, välju);
		menüü.getMenus().addAll(menüüFail);
		return menüü;
	}

	private void looUusMäng() {
		mänguväli.getChildren().clear();
		mänguväli.getChildren().add(vbUusMäng);
	}

	private ImageView annaPilt(String tee, double laius, double kõrgus, boolean säilitaKuvasuhe) {
		return new ImageView(new Image(getClass().getResourceAsStream(tee), laius, kõrgus, säilitaKuvasuhe, true));
	}

	private ImageView annaPilt(String tee) {
		return new ImageView(new Image(getClass().getResourceAsStream(tee)));
	}

	private void logiKäik(int y, int x) {
		try {
			fw = new FileWriter(failTee, true);
			fw.write(y + "/" + x + ";");
			fw.close();
		} catch (IOException e) {
		}
	}

	public class Lahter extends Pane {
		private Mängija omanik;
		private Koordinaadid koordinaadid;

		public Lahter(Koordinaadid koordinaadid) {
			this.koordinaadid = koordinaadid;
			setStyle("-fx-border-color: black");
			this.setPrefSize(150, 150);
			this.setOnMouseClicked(e -> teeKäik());
		}

		public Mängija getOmanik() {
			return omanik;
		}

		public Koordinaadid getKoordinaadid() {
			return koordinaadid;
		}

		public void setKoordinaadid(Koordinaadid koordinaadid) {
			this.koordinaadid = koordinaadid;
		}

		public void setOmanik(Mängija omanik) {
			this.omanik = omanik;
			this.getChildren()
					.add(annaPilt("resources/" + (omanik.equals(punaneMängija) ? "punane.png" : "sinine.png"),
							this.getWidth(), this.getHeight(), true));
		}

		public void tyhjenda() {
			this.omanik = null;
			this.getChildren().clear();
		}

		// põhiline meetod mis tegeleb käigu lisamisega
		private void teeKäik() {
			if (omanik == null) {
				logiKäik(this.koordinaadid.getY(), this.koordinaadid.getX());
				setOmanik(aktiivneMängija);
				omanik.getKäigud()[this.koordinaadid.getY()][this.koordinaadid.getX()] = 1;
				// kontrollime kas aktiivne mängija on võitnud. Kui jah, siis vastav teade ja
				// peale seda algseis.
				if (aktiivneMängija.kasMängijaVõitnud(this.koordinaadid.getX(), this.koordinaadid.getY())) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Mäng läbi");
					alert.setContentText(aktiivneMängija.toString() + " võitis.\nJätkamiseks vajuta OK!");
					alert.showAndWait();
					aktiivneMängija.suurendaTulemus();
					looUusMäng();
				} else if (kasVäliTäis()) {
					Alert mängläbi = new Alert(AlertType.INFORMATION);
					mängläbi.setTitle("Mäng läbi");
					mängläbi.setContentText("Viik.\nJätkamiseks vajuta OK!");
					mängläbi.showAndWait();
					looUusMäng();
				} else {
					// kui aktiivne mängija ei võitnud, siis vahetame aktiivse mängija ära.
					vahetaAktiivneMängija();
					teavitus.setText(aktiivneMängija + " käib");
					// kui nüüd on aktiivne mängija arvuti, siis hakkame tema käiku ette valmistama
					// ja proovime käia
					if (aktiivneMängija.isKasArvuti()) {
						Koordinaadid pandavad;
						if (punaneMängija.onnupp().size() != 0) {
							while (true) {
								pandavad = arvutikäib();
								System.out.println(pandavad.toString());
								System.out.println(väljad.get(pandavad).getOmanik());
								if (väljad.get(pandavad).getOmanik() == null) {
									väljad.get(pandavad).teeKäik();
									break;
								}
								keelatudKoordinaadid.add(pandavad);
							}
						}
					}
				}
			}
		}
	}

	// meetod kontrollib kas Väli on täis. Kui leiab välja mis ei ole täidetud, siis
	// false
	private boolean kasVäliTäis() {
		for (Lahter väli : väljad.values()) {
			if (väli.getOmanik() == null) {
				return false;
			}
		}
		return true;
	}

	public Koordinaadid arvutikäib() {
		Koordinaadid pandavad = null;
		List<Koordinaadid> punasekoordinaadid4 = punaneMängija.onVertikaalselt_4();
		List<Koordinaadid> sinisekoordinaadid4 = sinineMängija.onVertikaalselt_4();
		List<Koordinaadid> punasekoordinaadid3 = punaneMängija.onVertikaalselt_3();
		List<Koordinaadid> sinisekoordinaadid3 = sinineMängija.onVertikaalselt_3();
		List<Koordinaadid> punasekoordinaadid2 = punaneMängija.onVertikaalselt_2();
		List<Koordinaadid> sinisekoordinaadid2 = sinineMängija.onVertikaalselt_2();
		List<Koordinaadid> punasekoordinaadid1 = punaneMängija.onnupp();
		List<Koordinaadid> sinisekoordinaadid1 = sinineMängija.onnupp();

		// lisame kõik meetodid
		for (Koordinaadid koordinaat : sinineMängija.onDiagonaal1_4()) {
			sinisekoordinaadid4.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onDiagonaal1_4()) {
			punasekoordinaadid4.add(koordinaat);
		}
		for (Koordinaadid koordinaat : sinineMängija.onDiagonaal1_3()) {
			sinisekoordinaadid3.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onDiagonaal1_3()) {
			punasekoordinaadid3.add(koordinaat);
		}
		for (Koordinaadid koordinaat : sinineMängija.onDiagonaal1_2()) {
			sinisekoordinaadid2.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onDiagonaal1_2()) {
			punasekoordinaadid2.add(koordinaat);
		}
		for (Koordinaadid koordinaat : sinineMängija.onDiagonaal2_4()) {
			sinisekoordinaadid4.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onDiagonaal2_4()) {
			punasekoordinaadid4.add(koordinaat);
		}
		for (Koordinaadid koordinaat : sinineMängija.onDiagonaal2_3()) {
			sinisekoordinaadid3.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onDiagonaal2_3()) {
			punasekoordinaadid3.add(koordinaat);
		}
		for (Koordinaadid koordinaat : sinineMängija.onDiagonaal2_2()) {
			sinisekoordinaadid2.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onDiagonaal2_2()) {
			punasekoordinaadid2.add(koordinaat);

		}
		for (Koordinaadid koordinaat : sinineMängija.onHorisontaalselt_4()) {
			sinisekoordinaadid4.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onHorisontaalselt_4()) {
			punasekoordinaadid4.add(koordinaat);
		}
		for (Koordinaadid koordinaat : sinineMängija.onHorisontaalselt_3()) {
			sinisekoordinaadid3.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onHorisontaalselt_3()) {
			punasekoordinaadid3.add(koordinaat);

		}
		for (Koordinaadid koordinaat : sinineMängija.onHorisontaalselt_2()) {
			sinisekoordinaadid2.add(koordinaat);
		}
		for (Koordinaadid koordinaat : punaneMängija.onHorisontaalselt_2()) {
			punasekoordinaadid2.add(koordinaat);
		}
		punasekoordinaadid1.removeAll(keelatudKoordinaadid);
		punasekoordinaadid2.removeAll(keelatudKoordinaadid);
		punasekoordinaadid3.removeAll(keelatudKoordinaadid);
		punasekoordinaadid4.removeAll(keelatudKoordinaadid);
		sinisekoordinaadid1.removeAll(keelatudKoordinaadid);
		sinisekoordinaadid2.removeAll(keelatudKoordinaadid);
		sinisekoordinaadid3.removeAll(keelatudKoordinaadid);
		sinisekoordinaadid4.removeAll(keelatudKoordinaadid);

		if (sinisekoordinaadid4.size() != 0) {
			System.out.println("a");
			Koordinaadid parim4 = leiaparim(sinisekoordinaadid4);
			pandavad = parim4;
		} else if (punasekoordinaadid4.size() != 0) {
			System.out.println("b");
			Koordinaadid parim4 = leiaparim(punasekoordinaadid4);
			pandavad = parim4;
		} else if (sinisekoordinaadid3.size() != 0) {
			System.out.println("c");
			Koordinaadid parim4 = leiaparim(sinisekoordinaadid3);
			pandavad = parim4;
		} else if (punasekoordinaadid3.size() != 0) {
			System.out.println("d");
			Koordinaadid parim4 = leiaparim(punasekoordinaadid3);
			pandavad = parim4;
		} else if (sinisekoordinaadid2.size() != 0) {
			System.out.println("e");
			Koordinaadid parim4 = leiaparim(sinisekoordinaadid2);
			pandavad = parim4;
		} else if (punasekoordinaadid2.size() != 0) {
			System.out.println("f");
			Koordinaadid parim4 = leiaparim(punasekoordinaadid2);
			pandavad = parim4;
		} else if (sinisekoordinaadid1.size() != 0) {
			System.out.println("g");
			Koordinaadid parim4 = leiaparim(sinisekoordinaadid1);
			pandavad = parim4;
		} else if (punasekoordinaadid1.size() != 0) {
			System.out.println("h");
			Koordinaadid parim4 = leiaparim(punasekoordinaadid1);
			pandavad = parim4;
		}
		eemaldaantudelemendid(pandavad, sinisekoordinaadid4);
		eemaldaantudelemendid(pandavad, punasekoordinaadid4);
		eemaldaantudelemendid(pandavad, sinisekoordinaadid3);
		eemaldaantudelemendid(pandavad, punasekoordinaadid3);
		eemaldaantudelemendid(pandavad, punasekoordinaadid2);
		eemaldaantudelemendid(pandavad, sinisekoordinaadid2);
		eemaldaantudelemendid(pandavad, sinisekoordinaadid1);
		eemaldaantudelemendid(pandavad, punasekoordinaadid1);
		return pandavad;
	}

	private Koordinaadid leiaparim(List<Koordinaadid> antudlist) {
		int parim = 1;
		int mitmeselement = 0;
		for (int i = 0; i < antudlist.size(); i++) {
			int hetkel = 0;
			for (int j = 0; j < antudlist.size(); j++) {
				if (antudlist.get(i).equals(antudlist.get(j))) {
					hetkel += 1;
				}
				if (hetkel > parim) {
					parim = hetkel;
					mitmeselement = i;
				}
			}
		}
		return antudlist.get(mitmeselement);
	}

	private void eemaldaantudelemendid(Koordinaadid antud, List<Koordinaadid> koordinaadid) {
		for (int i = 0; i < koordinaadid.size(); i++) {
			try {
				if (koordinaadid.get(i).equals(antud)) {
					koordinaadid.remove(i);
					System.out.println("eemaldasin midagi");
					eemaldaantudelemendid(antud, koordinaadid);
				}
			} catch (ArrayIndexOutOfBoundsException Listindexoutofbounds) {
				System.out.println("erind toimib");
				break;
			}
		}
	}
}