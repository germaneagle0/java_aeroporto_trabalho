package TrabalhoAeroporto;

public class Aeroporto {
    public double latitude;
    public double longitude;
    public String name;
    public String iata;
    public String cidade;
    public String estado;
    Aeroporto(double latitude, double longitude, String name, String iata, String cidade, String estado) {
        this.latitude=latitude;
        this.longitude=longitude;
        this.name=name;
        this.iata=iata;
        this.cidade = cidade;
        this.estado = estado;
    }
    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    public double getDistance(Aeroporto A, Aeroporto B) {
        return distance(A.latitude, A.longitude, B.latitude, B.longitude, 'K');
    }
}
