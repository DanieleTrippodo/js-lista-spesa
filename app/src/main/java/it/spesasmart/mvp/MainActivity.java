package it.spesasmart.mvp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends Activity implements LocationListener {
    final NumberFormat eur=NumberFormat.getCurrencyInstance(Locale.ITALY);
    final String[] products={"Coca-Cola Original 1,5 L","AIA Petto di pollo 400 g","Banane Chiquita 1 kg","Barilla Spaghetti n.5 500 g","Parmalat Latte intero 1 L","Pavesi Gocciole 500 g"};
    final String[] stores={"Lidl","Eurospin","Conad","MD"};
    final String[] addresses={"Via Roma, Palermo","Via Leonardo da Vinci, Palermo","Via Notarbartolo, Palermo","Viale Regione Siciliana, Palermo"};
    final double[][] prices={{1.99,5.49,1.59,.99,1.25,2.69},{1.79,5.79,1.39,1.05,1.19,2.79},{2.09,4.99,1.69,.89,1.39,2.49},{1.89,5.29,1.45,.95,1.09,2.59}};
    final double[][] coords={{38.1128,13.3660},{38.1327,13.3358},{38.1280,13.3470},{38.1190,13.3200}};
    final ArrayList<Integer> list=new ArrayList<>();
    ArrayAdapter<String> listAdapter; Spinner productSpinner,maxStores; TextView locationText,result; Location current;

    public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(Color.rgb(24,103,63)); setDemoLocation();
        ScrollView scroll=new ScrollView(this); LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(28,28,28,40); root.setBackgroundColor(Color.rgb(246,248,246)); scroll.addView(root);
        TextView title=t("SpesaSmart",30,true); title.setTextColor(Color.rgb(24,103,63)); root.addView(title);
        TextView sub=t("Confronta prodotti identici tra Lidl, Eurospin, Conad e MD",15,false); root.addView(sub);
        TextView demo=t("VERSIONE DIMOSTRATIVA · prezzi simulati",12,true); demo.setTextColor(Color.rgb(145,88,0)); demo.setBackgroundColor(Color.rgb(255,239,204)); demo.setPadding(12,10,12,10); root.addView(demo,mp());
        root.addView(section("1. Posizione")); locationText=t("Posizione demo: centro di Palermo",14,false); root.addView(locationText);
        Button gps=btn("Usa la mia posizione GPS"); gps.setOnClickListener(v->gps()); root.addView(gps,mp());
        root.addView(section("2. Lista della spesa")); productSpinner=new Spinner(this); productSpinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,products)); root.addView(productSpinner,mp());
        Button add=btn("+ Aggiungi prodotto"); add.setOnClickListener(v->addProduct()); root.addView(add,mp());
        TextView hint=t("Tocca un prodotto per rimuoverlo.",12,false); hint.setTextColor(Color.GRAY); root.addView(hint);
        ListView lv=new ListView(this); listAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,new ArrayList<>()); lv.setAdapter(listAdapter); lv.setOnItemClickListener((a,v,p,id)->{list.remove(p);refresh();}); root.addView(lv,new LinearLayout.LayoutParams(-1,300));
        root.addView(section("3. Preferenze")); maxStores=new Spinner(this); maxStores.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"1 supermercato","2 supermercati","3 supermercati"})); maxStores.setSelection(1); root.addView(maxStores,mp());
        TextView travel=t("Costo viaggio stimato: €0,12/km.",12,false); travel.setTextColor(Color.GRAY); root.addView(travel);
        Button calc=btn("Calcola la spesa più conveniente"); calc.setOnClickListener(v->calculate()); root.addView(calc,mp());
        root.addView(section("Risultato")); result=t("Aggiungi almeno un prodotto e premi Calcola.",14,false); result.setBackgroundColor(Color.WHITE); result.setPadding(18,18,18,18); root.addView(result,mp()); setContentView(scroll);
    }
    TextView t(String s,int size,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);if(bold)v.setTypeface(null,1);return v;}
    TextView section(String s){TextView v=t(s,19,true);v.setTextColor(Color.rgb(24,103,63));v.setPadding(0,24,0,8);return v;}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(Color.WHITE);b.setBackgroundColor(Color.rgb(24,103,63));return b;}
    LinearLayout.LayoutParams mp(){return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);}
    void addProduct(){int i=productSpinner.getSelectedItemPosition();if(!list.contains(i)){list.add(i);refresh();}else Toast.makeText(this,"Prodotto già presente",Toast.LENGTH_SHORT).show();}
    void refresh(){ArrayList<String> x=new ArrayList<>();for(int i:list)x.add("• "+products[i]);listAdapter.clear();listAdapter.addAll(x);}
    void gps(){if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},7);return;} LocationManager m=(LocationManager)getSystemService(LOCATION_SERVICE);try{m.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);Location l=m.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);if(l!=null)onLocationChanged(l);}catch(Exception e){Toast.makeText(this,"GPS non disponibile: uso Palermo",Toast.LENGTH_LONG).show();}}
    public void onLocationChanged(Location l){current=l;locationText.setText(String.format(Locale.ITALY,"Posizione rilevata: %.5f, %.5f",l.getLatitude(),l.getLongitude()));}
    public void onProviderEnabled(String p){} public void onProviderDisabled(String p){} public void onStatusChanged(String p,int s,Bundle e){}
    public void onRequestPermissionsResult(int r,String[] p,int[] g){super.onRequestPermissionsResult(r,p,g);if(r==7&&g.length>0&&g[0]==PackageManager.PERMISSION_GRANTED)gps();}
    void setDemoLocation(){current=new Location("demo");current.setLatitude(38.1157);current.setLongitude(13.3615);}
    void calculate(){if(list.isEmpty()){Toast.makeText(this,"Aggiungi almeno un prodotto",Toast.LENGTH_SHORT).show();return;}int max=maxStores.getSelectedItemPosition()+1;Plan best=null;for(int mask=1;mask<16;mask++){if(Integer.bitCount(mask)>max)continue;Plan p=evaluate(mask);if(best==null||p.total<best.total)best=p;}render(best);}
    Plan evaluate(int mask){Map<Integer,ArrayList<Integer>> assigned=new LinkedHashMap<>();double subtotal=0;for(int prod:list){int bs=-1;double bp=999;for(int s=0;s<4;s++)if((mask&(1<<s))!=0&&prices[s][prod]<bp){bp=prices[s][prod];bs=s;}assigned.computeIfAbsent(bs,k->new ArrayList<>()).add(prod);subtotal+=bp;}double km=0;for(int s:assigned.keySet())km+=2*h(current.getLatitude(),current.getLongitude(),coords[s][0],coords[s][1]);double travel=km*.12;return new Plan(assigned,subtotal,km,travel,subtotal+travel);}
    void render(Plan p){StringBuilder o=new StringBuilder("SOLUZIONE CONSIGLIATA\n\n");for(Map.Entry<Integer,ArrayList<Integer>> e:p.a.entrySet()){int s=e.getKey();o.append(stores[s]).append(" — ").append(addresses[s]).append("\n");double sub=0;for(int prod:e.getValue()){o.append("  • ").append(products[prod]).append("\n    ").append(eur.format(prices[s][prod])).append("\n");sub+=prices[s][prod];}o.append("  Subtotale: ").append(eur.format(sub)).append("\n\n");}o.append("Prodotti: ").append(eur.format(p.sub)).append("\nPercorso stimato: ").append(String.format(Locale.ITALY,"%.1f km",p.km)).append("\nCosto viaggio: ").append(eur.format(p.travel)).append("\nTOTALE REALE: ").append(eur.format(p.total)).append("\n\nPrezzi dimostrativi: il backend reale sarà aggiunto nella versione successiva.");result.setText(o.toString());}
    double h(double a,double b,double c,double d){double r=6371,x=Math.toRadians(c-a),y=Math.toRadians(d-b);double q=Math.sin(x/2)*Math.sin(x/2)+Math.cos(Math.toRadians(a))*Math.cos(Math.toRadians(c))*Math.sin(y/2)*Math.sin(y/2);return r*2*Math.atan2(Math.sqrt(q),Math.sqrt(1-q));}
    static class Plan{Map<Integer,ArrayList<Integer>> a;double sub,km,travel,total;Plan(Map<Integer,ArrayList<Integer>>a,double s,double k,double t,double x){this.a=a;sub=s;km=k;travel=t;total=x;}}
}
