import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Grafo {
	HashMap<Integer, ArrayList<Edge>> map = new HashMap<>();
	ArrayList<Integer> qFinais = new ArrayList<Integer>();

	public void addEdge(int origem, int destino, String read, String write, String move) {
		if (!hasVertex(origem)) {
			addVertex(origem);
		}

		if (!hasVertex(destino)) {
			addVertex(destino);
		}

		map.get(origem).add(new Edge(origem, destino, read, write, move));

	}

	public boolean hasVertex(int no) {

		if (map.containsKey(no)) {
//				System.out.println("TEM");

			return true;
		}
//			System.out.println("Ñ TEM");
		return false;
	}

	public void addVertex(int vertex) {
		map.put(vertex, new ArrayList<Edge>());
	}

	public boolean hasEdge(int origem, int destino, String read, String write, String move) {
		if (hasVertex(origem)) {
			Edge edge = new Edge(origem, destino, read, write, move);

			for (int i = 0; i < map.get(origem).size(); i++) {
				if (map.get(origem).get(i).equals(edge)) {
					return true;
				}
			}

			return false;

		}
		return false;
	}

	public ArrayList<Edge> getEdges(int vertex) {
		return map.get(vertex);
	}

	public void addQfinal(int no) {
		if (!qFinais.contains(no)) {
			qFinais.add(no);
		}
	}

	public boolean isQFinal(int no) {
		return qFinais.contains(no);
	}

}

class Edge {
	int origem;
	int destino;
	String read;
	String write;
	String move;

	public Edge(int origem, int destino, String read, String write, String move) {
		this.origem = origem;
		this.destino = destino;
		this.read = read;
		this.write = write;
		this.move = move;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Edge edge = (Edge) o;
		return origem == edge.origem && destino == edge.destino && read.equals(edge.read) && write.equals(edge.write)
				&& move.equals(edge.move);
	}
}

public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException {

		Grafo grafo = new Grafo();

		Scanner entrada = new Scanner(System.in);
		int from, to;
		String read;
		String write;
		String move;
		int id;
		Object isFinal;

		ArrayList<String> tape = new ArrayList<String>();
		ArrayList<String> tapeTwo1 = new ArrayList<String>();
		ArrayList<String> tapeTwo2 = new ArrayList<String>();

		long inicio;
		long fim;

		try {
			File file = new File("./src/turingMachineOneTape.jff");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(file);
			document.getDocumentElement().normalize();
			NodeList nList;

			nList = document.getElementsByTagName("state");
//          System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
//              System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					id = Integer.parseInt(eElement.getAttributeNode("name").getNodeValue().replaceFirst("q", ""));
					isFinal = eElement.getElementsByTagName("final").item(0);

					if (isFinal != null) {
						grafo.addQfinal(id);
					}

					// System.out.println(id);
					// System.out.println(isFinal);

				}

			}

//            System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
			nList = document.getElementsByTagName("transition");
//            System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					read = eElement.getElementsByTagName("read").item(0).getTextContent();
					write = eElement.getElementsByTagName("write").item(0).getTextContent();
					move = eElement.getElementsByTagName("move").item(0).getTextContent();
					from = Integer.parseInt(eElement.getElementsByTagName("from").item(0).getTextContent());
					to = Integer.parseInt(eElement.getElementsByTagName("to").item(0).getTextContent());

					grafo.addEdge(from, to, read, write, move);

//                    tabelaHash.put(from, to);
//					System.out.println(read);
//					System.out.println(write);
//					System.out.println(move);
//                    System.out.println("From : " + eElement.getElementsByTagName("from").item(0).getTextContent());
//                    System.out.println("To : " + eElement.getElementsByTagName("to").item(0).getTextContent());

				}

			}

		} catch (IOException e) {
			System.out.println(e);
		}

		System.out.println("Digite a sequência de dígitos para validarmos");
		String respUsuario = entrada.next();
		boolean aux;
		boolean valido = true;
		int indiceAuxFrom = 0;
//        int indiceAuxTo = 1;

		System.out.println("UMA FITA:");
		inicio = System.currentTimeMillis();

		int indiceFita = 0;
		// preencher fita
		tape.add("B");
		for (int i = 0; i < respUsuario.length(); i++) {
			tape.add(Character.toString(respUsuario.charAt(i)));
		}
		tape.add("B");

		while (tape.size() != indiceFita) {

			ArrayList<Edge> edges = grafo.getEdges(indiceAuxFrom);
			aux = false;

			if (grafo.hasVertex(indiceAuxFrom)) {

				String caractere = tape.get(indiceFita);
				for (int j = 0; j < edges.size(); j++) {
					if (edges.get(j).read.equals(caractere)) {
						aux = true;

						indiceAuxFrom = edges.get(j).destino;

//        				System.out.println(edges.get(j).weight + " == " + caractere);
						tape.set(indiceFita, edges.get(j).write);

						if (edges.get(j).move.equals("R")) {
							indiceFita++;
						} else if (edges.get(j).move.equals("L")) {
							indiceFita--;
						}

//						for (int i = 0; i < tape.size(); i++) {
//							System.out.print(tape.get(i) + " ");
//						}
//						System.out.println();
					}
				}

			} else {
				valido = false;
			}

			if (aux == false) {
				break;
			}

		}

//		for (int i = 0; i < tape.size(); i++) {
//			System.out.print(tape.get(i) + " ");
//		}
//		System.out.println();

		if (valido && grafo.isQFinal(indiceAuxFrom)) {
			System.out.println("Expressão válida");
		} else {
			System.out.println("Expressão não válida");
		}

		fim = System.currentTimeMillis();

		System.out.println("Tempo: " + (fim - inicio) + " ms");

		System.out.println();
		System.out.println("DUAS FITAS:");
		inicio = System.currentTimeMillis();

		aux = true;
		valido = true;
		indiceAuxFrom = 0;

		indiceFita = 0;

		tapeTwo1.add("B");
		for (int i = 0; i < respUsuario.length(); i++) {
			tapeTwo1.add(Character.toString(respUsuario.charAt(i)));
		}
		tapeTwo1.add("B");

		for (int i = 0; i < tapeTwo1.size(); i++) {
			tapeTwo2.add(tapeTwo1.get(i));
		}

		while (tapeTwo2.size() != indiceFita) {

			ArrayList<Edge> edges = grafo.getEdges(indiceAuxFrom);
			aux = false;

			if (grafo.hasVertex(indiceAuxFrom)) {

				String caractere = tapeTwo2.get(indiceFita);
				for (int j = 0; j < edges.size(); j++) {
					if (edges.get(j).read.equals(caractere)) {
						aux = true;

						indiceAuxFrom = edges.get(j).destino;

						tapeTwo2.set(indiceFita, edges.get(j).write);

						if (edges.get(j).move.equals("R")) {
							indiceFita++;
						} else if (edges.get(j).move.equals("L")) {
							indiceFita--;
						}

					}
				}

			} else {
				valido = false;
			}

			if (aux == false) {
				break;
			}

		}

//		for (int i = 0; i < tape.size(); i++) {
//			System.out.print(tape.get(i) + " ");
//		}
//		System.out.println();

		if (valido && grafo.isQFinal(indiceAuxFrom)) {
			System.out.println("Expressão válida");
		} else {
			System.out.println("Expressão não válida");
		}

		fim = System.currentTimeMillis();

		System.out.println("Tempo: " + (fim - inicio) + " ms");
	}
}