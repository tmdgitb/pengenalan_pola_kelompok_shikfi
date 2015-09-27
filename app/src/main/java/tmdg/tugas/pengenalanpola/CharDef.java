package tmdg.tugas.pengenalanpola;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilham1012 on 9/27/15.
 */
public class CharDef {
    private String character;
    private String chainCode;
    private String dirChainCode;
    private String relChainCode;
    private List<String> subChainCode=new ArrayList<>();

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

    public List<String> getSubChainCode() {
        return subChainCode;
    }

    public String getDirChainCode() {
        return dirChainCode;
    }

    public String getRelChainCode() {
        return relChainCode;
    }

    public void calcDirChainCode(){
        dirChainCode = "";

        for(int i=1; i < chainCode.length(); i++){
            int a = Integer.parseInt(chainCode.charAt(i) + "");
            int b = Integer.parseInt(chainCode.charAt(i - 1) + "");
            int y, mod;

            if(a < b){ mod = 8; }
            else{      mod = 0; }

            y = a - b + mod;

            if(0 < y && y < 4){
                dirChainCode = dirChainCode + "+";
            }else if( y > 4){
                dirChainCode = dirChainCode + "-";
            }
        }

        Log.v("chaincode_dir", dirChainCode);
    }

    public void calcRelChainCode(){
        relChainCode = "";

        for(int i=1; i < chainCode.length(); i++){
            int a = Integer.parseInt(chainCode.charAt(i) + "");
            int b = Integer.parseInt(chainCode.charAt(i - 1) + "");
            int mod;

            if(a == b){

            }else{
                if(a < b){ mod = 8; }
                else{      mod = 0; }
                relChainCode = relChainCode + (a - b + mod);
            }
        }

        Log.v("chaincode_rel", relChainCode);
    }
}
