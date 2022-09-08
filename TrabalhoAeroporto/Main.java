/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TrabalhoAeroporto;

import org.apache.commons.text.WordUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Main {
    static HashMap<String, ArrayList<String[]>> hash_est;
    static HashMap<String, HashMap<String, Double>> hash;
    public static void init() {
        System.out.println("Loading...");
        ConexaoSQL sql_data = new ConexaoSQL();
        ArrayList<Aeroporto> aeroportos = sql_data.getConexaoMySQL();
        
        System.out.println("Hashing...");
        hash = set_hash(aeroportos);
        hash_est = set_hash_localizacao(aeroportos);
        System.out.println("Finished!");
    }
    public static void main(String[] args) {
        // Load SQL
        System.out.println("Loading...");
        ConexaoSQL sql_data = new ConexaoSQL();
        ArrayList<Aeroporto> aeroportos = sql_data.getConexaoMySQL();

        // Hash Distances
        System.out.println("Hashing...");
        hash = set_hash(aeroportos);
        System.out.println("Finished!");
        press_enter();
        
        question(hash, aeroportos);
        // Query User Data
    }
    public static Set<String> getEstados() {
        return hash_est.keySet();
    }
    public static ArrayList<String[]> getCidades(String estado) {
        return hash_est.get(estado);
    }
    public static ArrayList<String> getSolution(String iata_origem, String iata_destino) {
        return dijkstra(iata_origem, iata_destino, hash);
    }
    private static HashMap<String, ArrayList<String[]>> set_hash_localizacao(ArrayList<Aeroporto> arr) {
        HashMap<String, ArrayList<String[]>> result =  new HashMap<>();
        for (Aeroporto aeroporto: arr) {
            String estado = WordUtils.capitalize(removerAcentos(aeroporto.estado));
            String cidade = WordUtils.capitalize(removerAcentos(aeroporto.cidade));
            String iata = aeroporto.iata;
            if (result.get(estado) == null) {
                ArrayList<String[]> list_for_state = new ArrayList<>();
                result.put(estado, list_for_state);
            }
            ArrayList<String[]> list_for_state = result.get(estado);
            String[] city_iata = new String[2];
            city_iata[0] = cidade;
            city_iata[1] = iata;
            list_for_state.add(city_iata);
        }
        return result;
    }
    private static void printSolution(ArrayList<String> solution, int spacer) {
        System.out.println("Menor caminho não linear: ");
        for (int i = 0; i < solution.size(); i++) {
            int k = solution.size() - i - 1;
            if (i % spacer != 0) {
                System.out.print(" -> ");
            }
            System.out.print(solution.get(k));
            if (i % spacer == spacer - 1) {
                System.out.println();
            }
        }
        if (solution.size() % spacer != 0) {
            System.out.println();
        }
        press_enter();
    }
    private static void show_estados(HashMap<String, ArrayList<String[]>> hash_estados, int spacer) {
        System.out.println("Estados possiveis: ");
        int i = 0;
        for (String key: hash_estados.keySet()) {
            boolean jump = false;
            i += key.length();
            if (i > spacer) {
                i = 0;
                jump = true;
            }
            System.out.print(key);
            if (jump) {
                System.out.println();
            }
            else {
                System.out.print(" | ");
            }

        }
        if (i != 0) {
            System.out.println();
        }
    }
    private static void question(HashMap<String, HashMap<String, Double>> hash, ArrayList<Aeroporto> arr) {
        Scanner scan = new Scanner(System.in);
        HashMap<String, ArrayList<String[]>> hash_estados = set_hash_localizacao(arr);
        while(true) {
            String a = choose_aeroporto(hash_estados,true,scan,hash);
            String b = choose_aeroporto(hash_estados, false,scan,hash);
            if (a.equals(b)) {
                System.out.println("Repeated IATAs not allowed!");
                press_enter();
                continue;
            }
            ArrayList<String> solution = dijkstra(a, b, hash);
            printSolution(solution, 3);
            System.out.println("Gostaria de sair do programa? (s/n)");
            String resp = scan.next().toLowerCase();
            if (resp.equals("s")) {
                System.out.println("Boa viagem!");
                break;
            }
        }
    }
    private static ArrayList<String> dijkstra(String a, String b, HashMap<String, HashMap<String, Double>> hash) {
        System.out.println("Dijkstra: " +a+" -> " + b);
        ArrayList<String> solution = new ArrayList<>();
        HashMap<String, Integer> distances = new HashMap<>();
        PriorityQueue<Queue> pq = new PriorityQueue<>();
        HashMap<String, String> path_backtracing = new HashMap<>();
        pq.offer(new Queue(a, 0));
        distances.put(a, 0);

        while (!pq.isEmpty()) {
            Queue q = pq.poll();
            String name = q.name;
            Integer distance = q.total_distance;
            System.out.println("Node: " +name+" - Total Distance: " + distance);

            if (name.equals(b)) {
                System.out.println();
                System.out.println("Arrived!");
                String navigator = b;
                while(!navigator.equals(a)) {
                    solution.add(navigator);
                    navigator = path_backtracing.get(navigator);
                }
                solution.add(navigator);
                break;
            }
            if (distances.get(name) < distance) continue;
            HashMap<String,Double> ligacoes = hash.get(name);
            for (String key : ligacoes.keySet()) {
                if (name.equals(a) && key.equals(b)) {
                    continue;
                }
                Integer distTo = getDistKm(name, key, hash);
                if (distances.get(key) == null) {
                    path_backtracing.put(key, name);
                    distances.put(key, distances.get(name) + distTo);
                    pq.offer(new Queue(key, distTo));
                }
                else if (distances.get(key) > distances.get(name) + distTo) {
                    path_backtracing.put(key, name);
                    distances.put(key, distances.get(name) + distTo);
                    pq.offer(new Queue(key, distTo));
                }
            }
        }

        return solution;
    }
    private static int getDistKm(String a, String b, HashMap<String, HashMap<String, Double>> hash) {
        return (int) Math.round(hash.get(a).get(b));
    }
    private static boolean false_string(String iata, HashMap<String, HashMap<String, Double>> hash) {
        if (iata.length() != 3) {
            System.out.println("Invalid input!");
            return true;
        }
        if (hash.get(iata) == null) {
            System.out.println("Invalid IATA!");
            return true;
        }
        return false;
    }
    private static void press_enter() {
        System.out.println("Press enter to continue!");
        try{System.in.read();}
        catch(Exception e){}
    }
    private static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
    private static String get_estado(HashMap<String, ArrayList<String[]>> hash_estados, Scanner scan) {
        String estado;
        show_estados(hash_estados, 50);
        while (true) {
            System.out.println("\n-> Escolha um Estado: ");
            String est = WordUtils.capitalize(removerAcentos(scan.nextLine().toLowerCase()));
            System.out.println("Voce escolheu: "+ est);
            if (hash_estados.get(est) == null) {
                System.out.println("Estado invalido!");
                press_enter();
                continue;
            }
            estado = est;
            break;
        }
        System.out.println("Estado escolhido!");
        System.out.println("Escolha as 3 letras correspondentes a cidade!");
        press_enter();
        return estado;
    }
    private static void show_cidades(ArrayList<String[]> cidades, int spacer, String estado) {
        System.out.println("Cidades possiveis de " + estado + ":");
        int i = 0;
        for (String[] cidade: cidades) {
            boolean jump = false;
            // Padrao de texto: "<cidade> (XXX)", SIZE = len(cidade) + 6
            i += (cidade[0].length() + 6);
            if (i > spacer) {
                i = 0;
                jump = true;
            }
            System.out.print(cidade[0] + " ("+cidade[1]+")");
            if (jump) {
                System.out.println();
            }
            else {
                System.out.print(" | ");
            }
        }
        if (i != 0) {
            System.out.println();
        }
    }
    private static String get_cidade(ArrayList<String[]> cidades, Scanner scan, String estado, HashMap<String, HashMap<String, Double>> hash) {
        String cidade;
        show_cidades(cidades, 40, estado);
        while (true) {
            System.out.println("\n-> Escolha a IATA de uma Cidade:");
            String iata = removerAcentos(scan.nextLine().toUpperCase());
            System.out.println("Voce escolheu: "+ iata);
            if (false_string(iata, hash)) {
                System.out.println("Cidade invalida!");
                press_enter();
                continue;
            }
            cidade = iata;
            break;
        }
        System.out.println("Cidade escolhida!");
        press_enter();
        return cidade;
    }
    private static String choose_aeroporto(HashMap<String, ArrayList<String[]>> hash_estados, boolean first_choice, Scanner scan, HashMap<String, HashMap<String, Double>> hash) {
        System.out.println("Escolha o Aeroporto " + ((first_choice) ? "Origem" : "Destino" ) + ":");
        press_enter();
        String estado = get_estado(hash_estados,scan);
        return get_cidade(hash_estados.get(estado),scan,estado,hash);
    }
    private static HashMap<String,HashMap<String, Double>> set_hash(ArrayList<Aeroporto> arr) {
        HashMap<String,HashMap<String, Double>> hash = new HashMap<>();
        for (Aeroporto a: arr) {
            HashMap<String, Double> data_a = new HashMap<>();
            for (Aeroporto b: arr) {
                if (a.iata.equals(b.iata)) {
                    continue;
                }
                double dist = a.getDistance(a,b);
                data_a.put(b.iata, dist);
            }
            hash.put(a.iata, data_a);
        }
        return hash;
    }
}

