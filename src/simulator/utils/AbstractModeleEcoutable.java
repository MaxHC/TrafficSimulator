/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulator.utils;

import java.util.ArrayList;

/**
 *
 * @author 21811412
 */
public abstract class AbstractModeleEcoutable implements ModeleEcoutable {
    protected ArrayList<EcouteurModele> ecouteurs = new ArrayList<EcouteurModele>();

    @Override
    public void ajoutEcouteur(EcouteurModele e) {
        this.ecouteurs.add(e);
    }

    @Override
    public void retraitEcouteur(EcouteurModele e) {
        this.ecouteurs.remove(e);
    }
    
    public void fireChangement() {
        ecouteurs.forEach(e -> {
            e.modeleMisAJour(this);
        });
    }
}
