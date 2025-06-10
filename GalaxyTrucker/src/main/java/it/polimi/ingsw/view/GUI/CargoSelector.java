package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.ComponentType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static it.polimi.ingsw.model.enumerates.ComponentType.BlueStorage;
import static it.polimi.ingsw.model.enumerates.ComponentType.RedStorage;

public class CargoSelector {

    private int selectedCargoIndex = -1;
    private CountDownLatch latch;

    public int askCargo(List<Cargo> cargos) {
        // Reset della selezione
        selectedCargoIndex = -1;
        latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            showCargoSelector(cargos);
        });

        // Attende che l'utente faccia una scelta
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }

        return selectedCargoIndex;
    }

    private void showCargoSelector(List<Cargo> cargos) {
        // Creazione dello Stage popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Seleziona Cargo");
        popupStage.setResizable(false);

        // Container principale
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");

        // Titolo
        Label titleLabel = new Label("Scegli quale cargo vuoi Posizionare:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Griglia per i bottoni dei cargo
        GridPane cargoGrid = new GridPane();
        cargoGrid.setHgap(15);
        cargoGrid.setVgap(15);
        cargoGrid.setAlignment(Pos.CENTER);

        // Creazione dei bottoni per ogni cargo
        int columns = Math.min(3, cargos.size()); // Massimo 3 colonne
        for (int i = 0; i < cargos.size(); i++) {
            Cargo cargo = cargos.get(i);
            Button cargoButton = createCargoButton(cargo, i, popupStage);

            int row = i / columns;
            int col = i % columns;
            cargoGrid.add(cargoButton, col, row);
        }

        // Bottone "Nessun Cargo"
        Button noCargoButton = new Button("Nessun Cargo");
        noCargoButton.setStyle(
                "-fx-background-color: #dc3545; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 25 10 25; " +
                        "-fx-background-radius: 5;"
        );
        noCargoButton.setOnAction(e -> {
            selectedCargoIndex = -1;
            Platform.runLater(() -> {
                popupStage.close();
                latch.countDown();
            });
        });

        // Effetto hover per il bottone "Nessun Cargo"
        noCargoButton.setOnMouseEntered(e ->
                noCargoButton.setStyle(
                        "-fx-background-color: #bb2d3b; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 25 10 25; " +
                                "-fx-background-radius: 5;"
                )
        );
        noCargoButton.setOnMouseExited(e ->
                noCargoButton.setStyle(
                        "-fx-background-color: #dc3545; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 25 10 25; " +
                                "-fx-background-radius: 5;"
                )
        );

        // Assemblaggio del layout
        mainContainer.getChildren().addAll(titleLabel, cargoGrid, noCargoButton);

        // Creazione della scena
        Scene scene = new Scene(mainContainer);
        popupStage.setScene(scene);

        // Gestione della chiusura della finestra
        popupStage.setOnCloseRequest(e -> {
            selectedCargoIndex = -1;
            latch.countDown();
        });

        // Mostra il popup
        popupStage.show();
    }

    private Button createCargoButton(Cargo cargo, int index, Stage parentStage) {
        Button button = new Button();
        button.setPrefSize(120, 80);
        button.setText("Cargo " + "\n" + cargo);

        // Stile di base del bottone
        String baseStyle = getCargoButtonStyle(cargo);
        button.setStyle(baseStyle);

        // ActionListener per la selezione
        button.setOnAction(e -> {
            selectedCargoIndex = index;
            Platform.runLater(() -> {
                parentStage.close();
                latch.countDown();
            });
        });

        // Effetti hover
        String hoverStyle = getCargoButtonHoverStyle(cargo);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private String getCargoButtonStyle(Cargo cargo) {
        String backgroundColor = getCargoColorHex(cargo);
        String textColor = getTextColor(cargo);

        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;",
                backgroundColor, textColor
        );
    }

    private String getCargoButtonHoverStyle(Cargo cargo) {
        String backgroundColor = getCargoColorHoverHex(cargo);
        String textColor = getTextColor(cargo);

        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #000000; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8;",
                backgroundColor, textColor
        );
    }

    private String getCargoColorHex(Cargo cargo) {
        switch (cargo) {
            case Red:
                return "#dc3545"; // Rosso
            case Blue:
                return "#0d6efd"; // Blu
            case Green:
                return "#198754"; // Verde
            case Yellow:
                return "#ffc107"; // Giallo
            default:
                return "#6c757d"; // Grigio per tipi sconosciuti
        }
    }

    private String getCargoColorHoverHex(Cargo cargo) {
        switch (cargo) {
            case Red:
                return "#bb2d3b"; // Rosso più scuro
            case Blue:
                return "#0b5ed7"; // Blu più scuro
            case Green:
                return "#146c43"; // Verde più scuro
            case Yellow:
                return "#ffca2c"; // Giallo più chiaro
            default:
                return "#5a6268"; // Grigio più scuro
        }
    }

    private String getTextColor(Cargo cargo) {
        switch (cargo) {
            case Red:
            case Blue:
            case Green:
                return "white";
            case Yellow:
                return "black";
            default:
                return "white";
        }
    }

    public int askPlanet(List<List<Cargo>> planets, Set<Integer> planets_taken) {
        // Reset della selezione
        int selectedPlanetIndex = -1;
        CountDownLatch planetLatch = new CountDownLatch(1);
        final int[] result = {-1};

        Platform.runLater(() -> {
            showPlanetSelector(planets, planets_taken, planetLatch, result);
        });

        // Attende che l'utente faccia una scelta
        try {
            planetLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }

        return result[0];
    }

    private void showPlanetSelector(List<List<Cargo>> planets, Set<Integer> planets_taken,
                                    CountDownLatch planetLatch, int[] result) {
        // Creazione dello Stage popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Seleziona Pianeta");
        popupStage.setResizable(false);

        // Container principale
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");

        // Titolo
        Label titleLabel = new Label("Scegli quale pianeta vuoi conquistare:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Griglia per i bottoni dei pianeti
        GridPane planetGrid = new GridPane();
        planetGrid.setHgap(15);
        planetGrid.setVgap(15);
        planetGrid.setAlignment(Pos.CENTER);

        // Creazione dei bottoni per ogni pianeta
        int columns = Math.min(3, planets.size()); // Massimo 3 colonne
        for (int i = 0; i < planets.size(); i++) {
            List<Cargo> planetCargos = planets.get(i);
            boolean isPlanetTaken = planets_taken.contains(i);
            Button planetButton = createPlanetButton(i, planetCargos, isPlanetTaken, popupStage, planetLatch, result);

            int row = i / columns;
            int col = i % columns;
            planetGrid.add(planetButton, col, row);
        }

        // Bottone "Annulla"
        Button cancelButton = new Button("Annulla");
        cancelButton.setStyle(
                "-fx-background-color: #6c757d; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 25 10 25; " +
                        "-fx-background-radius: 5;"
        );
        cancelButton.setOnAction(e -> {
            result[0] = -1;
            Platform.runLater(() -> {
                popupStage.close();
                planetLatch.countDown();
            });
        });

        // Effetto hover per il bottone "Annulla"
        cancelButton.setOnMouseEntered(e ->
                cancelButton.setStyle(
                        "-fx-background-color: #5a6268; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 25 10 25; " +
                                "-fx-background-radius: 5;"
                )
        );
        cancelButton.setOnMouseExited(e ->
                cancelButton.setStyle(
                        "-fx-background-color: #6c757d; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 12px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 25 10 25; " +
                                "-fx-background-radius: 5;"
                )
        );

        // Assemblaggio del layout
        mainContainer.getChildren().addAll(titleLabel, planetGrid, cancelButton);

        // Creazione della scena
        Scene scene = new Scene(mainContainer);
        popupStage.setScene(scene);

        // Gestione della chiusura della finestra
        popupStage.setOnCloseRequest(e -> {
            result[0] = -1;
            planetLatch.countDown();
        });

        // Mostra il popup
        popupStage.show();
    }

    private Button createPlanetButton(int planetIndex, List<Cargo> cargos, boolean isPlanetTaken,
                                      Stage parentStage, CountDownLatch planetLatch, int[] result) {
        Button button = new Button();
        button.setPrefSize(150, 100);

        // Creazione del testo del bottone con i cargo
        StringBuilder buttonText = new StringBuilder();
        buttonText.append("Pianeta ").append(planetIndex).append("\n");

        if (isPlanetTaken) {
            buttonText.append("OCCUPATO");
        } else {
            buttonText.append("DISPONIBILE\n");
            // Mostra i cargo del pianeta
            for (int i = 0; i < cargos.size(); i++) {
                buttonText.append(cargos.get(i));
                if (i < cargos.size() - 1) {
                    buttonText.append(", ");
                }
            }
        }

        button.setText(buttonText.toString());

        // Stile del bottone basato sullo stato
        String baseStyle = getPlanetButtonStyle(isPlanetTaken);
        button.setStyle(baseStyle);

        // ActionListener per la selezione (solo se il pianeta è disponibile)
        if (!isPlanetTaken) {
            button.setOnAction(e -> {
                result[0] = planetIndex;
                Platform.runLater(() -> {
                    parentStage.close();
                    planetLatch.countDown();
                });
            });

            // Effetti hover solo per pianeti disponibili
            String hoverStyle = getPlanetButtonHoverStyle();
            button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
            button.setOnMouseExited(e -> button.setStyle(baseStyle));
        } else {
            // Disabilita il bottone per pianeti occupati
            button.setDisable(true);
        }

        return button;
    }

    private String getPlanetButtonStyle(boolean isPlanetTaken) {
        if (isPlanetTaken) {
            // Stile per pianeti occupati (grigio e disabilitato)
            return "-fx-background-color: #dc3545; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #dee2e6; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 8; " +
                    "-fx-opacity: 0.6;";
        } else {
            // Stile per pianeti disponibili (verde)
            return "-fx-background-color: #198754; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #dee2e6; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 8;";
        }
    }

    private String getPlanetButtonHoverStyle() {
        // Stile hover per pianeti disponibili (verde più scuro)
        return "-fx-background-color: #146c43; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 11px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #000000; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;";
    }

    // Aggiungere questi metodi alla classe CargoSelector

    private String removedCargoMessage = "";
    private CountDownLatch removalLatch;

    public void removeCargo(Ship ship) {
        List<Pair<Integer,Integer>> storage_with_red = new ArrayList<>();
        List<Pair<Integer,Integer>> other_storage = new ArrayList<>();
        List<Pair<Integer,Integer>> batteries = new ArrayList<>();

        // Analizza la nave per trovare cargo e batterie
        for(int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent card = ship.getComponent(i, j);

                if (card.getComponentType() == RedStorage && ((Storage)card).containsCargo(Cargo.Red)) {
                    storage_with_red.add(new Pair<>(i,j));
                    other_storage.add(new Pair<>(i, j));
                } else if ((card.getComponentType() == BlueStorage || card.getComponentType() == RedStorage) &&
                        (((Storage)card).getCargoCount() > 0)) {
                    other_storage.add(new Pair<>(i, j));
                } else if (card.getComponentType() == ComponentType.Battery && ((Battery)card).getStored() > 0) {
                    batteries.add(new Pair<>(i, j));
                }
            }
        }

        // Priorità 1: Rimuovi cargo rosso da RedStorage
        if (!storage_with_red.isEmpty()) {
            handleRedCargoRemoval(ship, storage_with_red);
        }
        // Priorità 2: Rimuovi altri cargo da storage
        else if (!other_storage.isEmpty()) {
            handleOtherCargoRemoval(ship, other_storage);
        }
        // Priorità 3: Rimuovi batterie
        else if (!batteries.isEmpty()) {
            handleBatteryRemoval(ship, batteries);
        }
        // Nessuna perdita
        else {
            showRemovalMessage("NON HAI PERSO NIENTE!!!", "Non avevi cargo o batterie disponibili!", "#28a745");
        }
    }

    private void handleRedCargoRemoval(Ship ship, List<Pair<Integer,Integer>> storage_with_red) {
        for (Pair<Integer, Integer> pos : storage_with_red) {
            CardComponent card = ship.getComponent(pos.getKey(), pos.getValue());
            if (((Storage)card).removeCargo(Cargo.Red)) {
                String message = String.format("HAI PERSO UN CARGO ROSSO a RIGA: %d COLONNA: %d",
                        pos.getKey(), pos.getValue());
                showRemovalMessage("Cargo Perso!", message, "#dc3545");
                return;
            }
        }
    }

    private void handleOtherCargoRemoval(Ship ship, List<Pair<Integer,Integer>> other_storage) {
        // Ordine di priorità: Yellow, Green, Blue
        Cargo[] cargoOrder = {Cargo.Yellow, Cargo.Green, Cargo.Blue};
        String[] cargoColors = {"#ffc107", "#198754", "#0d6efd"};
        String[] cargoNames = {"GIALLO", "VERDE", "BLU"};

        for (int cargoIndex = 0; cargoIndex < cargoOrder.length; cargoIndex++) {
            Cargo cargoType = cargoOrder[cargoIndex];
            for (Pair<Integer, Integer> pos : other_storage) {
                CardComponent card = ship.getComponent(pos.getKey(), pos.getValue());
                if (((Storage)card).removeCargo(cargoType)) {
                    String message = String.format("HAI PERSO UN CARGO %s a RIGA: %d COLONNA: %d",
                            cargoNames[cargoIndex], pos.getKey(), pos.getValue());
                    showRemovalMessage("Cargo Perso!", message, cargoColors[cargoIndex]);
                    return;
                }
            }
        }
    }

    private void handleBatteryRemoval(Ship ship, List<Pair<Integer,Integer>> batteries) {
        for (Pair<Integer, Integer> pos : batteries) {
            CardComponent card = ship.getComponent(pos.getKey(), pos.getValue());
            if (((Battery)card).getStored() > 0) {
                ((Battery)card).removeBattery();
                String message = String.format("HAI PERSO UNA BATTERIA a RIGA: %d COLONNA: %d",
                        pos.getKey(), pos.getValue());
                showRemovalMessage("Batteria Persa!", message, "#ff8c00");
                return;
            }
        }
    }

    private void showRemovalMessage(String title, String message, String color) {
        removalLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            showRemovalDialog(title, message, color);
        });

        // Attende che l'utente chiuda il messaggio
        try {
            removalLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void showRemovalDialog(String title, String message, String color) {
        // Creazione dello Stage popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle(title);
        popupStage.setResizable(false);

        // Container principale
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");

        // Icona e titolo
        Label titleLabel = new Label(title);
        titleLabel.setStyle(String.format(
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: %s;", color));

        // Messaggio
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        // Bottone OK
        Button okButton = new Button("OK");
        okButton.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 30 10 30; " +
                        "-fx-background-radius: 5;", color
        ));

        okButton.setOnAction(e -> {
            popupStage.close();
            removalLatch.countDown();
        });

        // Effetto hover per il bottone OK
        String hoverColor = darkenColor(color);
        okButton.setOnMouseEntered(e ->
                okButton.setStyle(String.format(
                        "-fx-background-color: %s; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 30 10 30; " +
                                "-fx-background-radius: 5;", hoverColor
                ))
        );
        okButton.setOnMouseExited(e ->
                okButton.setStyle(String.format(
                        "-fx-background-color: %s; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 30 10 30; " +
                                "-fx-background-radius: 5;", color
                ))
        );

        // Assemblaggio del layout
        mainContainer.getChildren().addAll(titleLabel, messageLabel, okButton);

        // Creazione della scena
        Scene scene = new Scene(mainContainer);
        popupStage.setScene(scene);

        // Gestione della chiusura della finestra
        popupStage.setOnCloseRequest(e -> {
            removalLatch.countDown();
        });

        // Mostra il popup e centra il focus sul bottone
        popupStage.show();
        okButton.requestFocus();
    }

    // Metodo helper per scurire i colori per l'effetto hover
    private String darkenColor(String hexColor) {
        switch (hexColor) {
            case "#dc3545": return "#bb2d3b"; // Rosso più scuro
            case "#ffc107": return "#e0a800"; // Giallo più scuro
            case "#198754": return "#146c43"; // Verde più scuro
            case "#0d6efd": return "#0b5ed7"; // Blu più scuro
            case "#28a745": return "#1e7e34"; // Verde successo più scuro
            case "#ff8c00": return "#e07600"; // Arancione più scuro
            default: return "#5a6268"; // Grigio scuro di default
        }
    }
}