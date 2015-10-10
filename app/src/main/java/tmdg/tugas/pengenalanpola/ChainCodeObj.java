package tmdg.tugas.pengenalanpola;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rini on 05/10/2015.
 */
public class ChainCodeObj {
    private String character;
    private String chainCode;
    private String kodeBelok;
    private List<ChainCodeObj> subChainCode=new ArrayList<>();

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getChainCode() {
        return chainCode;
    }

    public void setChainCode(String chainCode) {
        this.chainCode = chainCode;
    }

    public String getKodeBelok() {
        return kodeBelok;
    }

    public void setKodeBelok(String kodeBelok) {
        this.kodeBelok = kodeBelok;
    }

    public List<ChainCodeObj> getSubChainCode() {
        return subChainCode;
    }
}