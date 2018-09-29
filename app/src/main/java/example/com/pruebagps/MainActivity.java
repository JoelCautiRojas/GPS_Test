package example.com.pruebagps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;

public class MainActivity extends AppCompatActivity {

    private double latitud, longitud;
    private GoogleApiClient clientGPS;
    private Snackbar snak1, snak2;
    private RelativeLayout c;
    private Button bt;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView tv;
    private String mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        c = findViewById(R.id.contenedor);
        bt = findViewById(R.id.button);
        tv = findViewById(R.id.resultado);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mensaje = "";
                if (statusNetwork()) {
                    Log.d("ESTADO DE RED", "RED_ACTIVA________");
                    mensaje += "ESTADO DE RED : RED_ACTIVA________\n";
                    if (statusGPS()) {
                        Log.d("ESTADO DE GPS", "GPS_ACTIVO________");
                        mensaje += "ESTADO DE GPS : GPS_ACTIVO________\n";
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getLastLocation()
                                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Location> task) {
                                            Log.d("FUSED_PROVIDER", "COMPLETE________");
                                            mensaje += "FUSED_PROVIDER : COMPLETE________\n";
                                            tv.setText(mensaje);
                                        }
                                    })
                                    .addOnCanceledListener(new OnCanceledListener() {
                                        @Override
                                        public void onCanceled() {
                                            Log.d("FUSED_PROVIDER", "CANCELED________");
                                            mensaje += "FUSED_PROVIDER : CANCELED________\n";
                                            tv.setText(mensaje);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("FUSED_PROVIDER", "FAILURE________");
                                            mensaje += "FUSED_PROVIDER : FAILURE________\n";
                                            tv.setText(mensaje);
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            Log.d("FUSED_PROVIDER", "SUCCESS________");
                                            mensaje += "FUSED_PROVIDER : SUCCESS________\n";
                                            if (location != null) {
                                                Log.d("STATUS_UBICACION", "ENCONTRADA______");
                                                mensaje += "STATUS_UBICACION : ENCONTRADA________\n";
                                                latitud = location.getLatitude();
                                                longitud = location.getLongitude();
                                                if (isMockLocation(location)) {
                                                    Log.d("MOCK", "LA UBICACION ES FALSA______");
                                                    mensaje += "MOCK : LA UBICACION ES FALSA________\n";
                                                } else {
                                                    Log.d("MOCK", "LA UBICACION ES VERDADERA________");
                                                    mensaje += "MOCK : LA UBICACION ES VERDADERA________\n";
                                                }
                                                Log.d("COORDENADAS",String.valueOf(latitud)+"|"+String.valueOf(longitud));
                                                mensaje += "COORDENADAS : "+String.valueOf(latitud)+"|"+String.valueOf(longitud)+"________\n";
                                                tv.setText(mensaje);
                                            } else {
                                                Log.d("STATUS_UBICACION", "NULA______");
                                                mensaje += "STATUS_UBICACION : NULA________\n";
                                                latitud = 0.0;
                                                longitud = 0.0;
                                                Log.d("COORDENADAS",String.valueOf(latitud)+"|"+String.valueOf(longitud));
                                                mensaje += "COORDENADAS : "+String.valueOf(latitud)+"|"+String.valueOf(longitud)+"________\n";
                                                LocationRequest request = new LocationRequest();
                                                request.setInterval(10000);
                                                request.setFastestInterval(5000);
                                                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                                final LocationCallback callback = new LocationCallback(){
                                                    @Override
                                                    public void onLocationResult(LocationResult locationResult) {
                                                        if (locationResult == null) {
                                                            return;
                                                        }
                                                        for (Location location : locationResult.getLocations()) {
                                                            if(location != null){
                                                                latitud = location.getLatitude();
                                                                longitud = location.getLongitude();
                                                                fusedLocationProviderClient.removeLocationUpdates(this);
                                                                Log.d("SEGUNDA LLAMADA", String.valueOf(latitud)+"|"+String.valueOf(longitud));
                                                                mensaje += "SEGUNDA LLAMADA COORDENADAS : "+String.valueOf(latitud)+"|"+String.valueOf(longitud)+"________\n";
                                                                if (isMockLocation(location)) {
                                                                    Log.d("MOCK", "LA UBICACION ES FALSA______");
                                                                    mensaje += "MOCK : LA UBICACION ES FALSA________\n";
                                                                } else {
                                                                    Log.d("MOCK", "LA UBICACION ES VERDADERA________");
                                                                    mensaje += "MOCK : LA UBICACION ES VERDADERA________\n";
                                                                }
                                                                tv.setText(mensaje);
                                                            }
                                                        }
                                                    }
                                                };
                                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                    fusedLocationProviderClient.requestLocationUpdates(request, callback, null);
                                                    return;
                                                }
                                        }
                                    }
                                });
                        }
                    }else{
                        Log.d("ESTADO DE GPS","GPS_INACTIVO________");
                        mensaje += "ESTADO DE GPS : GPS_INACTIVO________\n";
                        tv.setText(mensaje);
                    }
                }else{
                    Log.d("ESTADO DE RED","RED_INACTIVA________");
                    mensaje += "ESTADO DE RED : RED_INACTIVA_______\n";
                    tv.setText(mensaje);
                }
            }
        });

        snak1 = Snackbar.make(c,"Necesitas otorgar permisos",Snackbar.LENGTH_INDEFINITE);
        snak1.setAction("Solicitar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerPeticion();
            }
        });
        snak2 = Snackbar.make(c,"Los permisos fueron otorgados correctamente",Snackbar.LENGTH_LONG);
        if(verificarPermisos()){
            iniciarApp();
        }else{
            justificarSolicitud();
        }
    }

    public boolean statusNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    private boolean statusGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d("PROVIDER STATUS","GPS_PROVIDER_ENABLED_______");
            mensaje += "PROVIDER STATUS : GPS_PROVIDER_ENABLED_______\n";
        }else{
            Log.d("PROVIDER STATUS","GPS_PROVIDER_DISABLED_______");
            mensaje += "PROVIDER STATUS : GPS_PROVIDER_DISABLED_______\n";
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.d("PROVIDER STATUS","NETWORK_PROVIDER_ENABLED_______");
            mensaje += "PROVIDER STATUS : NETWORK_PROVIDER_ENABLED_______\n";
        }else{
            Log.d("PROVIDER STATUS","NETWORK_PROVIDER_DISABLED_______");
            mensaje += "PROVIDER STATUS : NETWORK_PROVIDER_DISABLED_______\n";
        }
        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            Log.d("PROVIDER STATUS","PASSIVE_PROVIDER_ENABLED_______");
            mensaje += "PROVIDER STATUS : PASSIVE_PROVIDER_ENABLED_______\n";
        }else{
            Log.d("PROVIDER STATUS","PASSIVE_PROVIDER_DISABLED_______");
            mensaje += "PROVIDER STATUS : PASSIVE_PROVIDER_DISABLED_______\n";
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return true;
        }else{
            return false;
        }
    }

    private boolean isMockLocation(Location location) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return location.isFromMockProvider();
        } else {
            return !Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
    }

    private void justificarSolicitud() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,ACCESS_NETWORK_STATE) ||
           ActivityCompat.shouldShowRequestPermissionRationale(this,ACCESS_FINE_LOCATION)){
            snak1.show();
        }else{
            hacerPeticion();
        }
    }

    private void iniciarApp() {
        bt.setEnabled(true);
    }

    private boolean verificarPermisos() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        if(ActivityCompat.checkSelfPermission(this,ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void hacerPeticion() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_NETWORK_STATE,ACCESS_FINE_LOCATION},100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    snak2.show();
                    bt.setEnabled(true);
                }else{
                    snak1.show();
                    bt.setEnabled(false);
                }
                break;
        }
    }
}
