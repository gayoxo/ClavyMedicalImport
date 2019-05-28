/**
 * 
 */
package fdi.ucm.server.importparser.medical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import fdi.ucm.server.modelComplete.ImportExportDataEnum;
import fdi.ucm.server.modelComplete.ImportExportPair;
import fdi.ucm.server.modelComplete.LoadCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionAndLog;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementURL;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteResourceElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;

/**
 * @author Joaquin Gayoso Cabada
 *
 */
public class LoadCollectionMedical extends LoadCollection{


	
	
	
	
	private static ArrayList<ImportExportPair> Parametros;
	public static boolean consoleDebug=false;
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadCollectionMedical LC=new LoadCollectionMedical();
		LoadCollectionMedical.consoleDebug=true;
		
		ArrayList<String> AA=new ArrayList<String>();
		
		CompleteCollectionAndLog Salida=null;
		

		AA.add("dos/sample.txt");
		AA.add("dos/salida.xml");
		AA.add("dos/terminos.txt");
	
			 Salida=LC.processCollecccion(AA);
	
			
		
		if (Salida!=null)
			{
			
			System.out.println("Correcto");
			
			for (String warning : Salida.getLogLines())
				System.err.println(warning);

			
			try {
				String FileIO = System.getProperty("user.home")+"/"+System.currentTimeMillis()+".clavy";
				
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FileIO));

				oos.writeObject(Salida.getCollection());

				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			System.exit(0);
			
			}
		else
			{
			System.err.println("Error");
			System.exit(-1);
			}
	}

	
	public LoadCollectionMedical() {
		LoadCollectionMedical.consoleDebug=false;
	}
	
	
	@Override
	public CompleteCollectionAndLog processCollecccion(ArrayList<String> dateEntrada) {
		
		CompleteCollectionAndLog Salida= new CompleteCollectionAndLog();
		Salida.setLogLines(new ArrayList<String>());
		Salida.setCollection(new CompleteCollection("Medical", "Medical Cases procesed by Metamap"));
		
		String Sample_File = dateEntrada.get(0);
		String Salida_File = dateEntrada.get(1);
		String Termns_File = dateEntrada.get(2);
		
		List<String> DocumentosList=new LinkedList<String>();
		   HashMap<String,String> DocumentosListText=new HashMap<String,String>();
		   HashMap<String, HashMap<String,List<HashMap<String,HashSet<String>>>>> Supertabla=new HashMap<String, HashMap<String,List<HashMap<String,HashSet<String>>>>>();
			HashMap<String, HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>>> SupertablaUtt=new HashMap<String, HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>>>();
			HashMap<String, HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>>> SupertablaUtt_list=new HashMap<String, HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>>>();
			
			HashMap<String,HashMap<String,HashSet<String>>> SupertablaSemPos=new HashMap<String,HashMap<String,HashSet<String>>>();
			HashMap<String,HashMap<String,HashSet<String>>> SupertablaSemNeg=new HashMap<String,HashMap<String,HashSet<String>>>();
		   
		   
		   System.out.println("//Procesando el Sample");
		   
		   try {
			   LoadSampleTXT(DocumentosList,DocumentosListText,Sample_File);
		} catch (Exception e) {
			e.printStackTrace();
			StackTraceElement[] lista = e.getStackTrace();
			for (StackTraceElement stackTraceElement : lista) {
				Salida.getLogLines().add(stackTraceElement.toString());
			}
			return Salida;
		}
		   
		   try {
				procesaSalida(DocumentosList,DocumentosListText,Supertabla,SupertablaUtt,SupertablaSemNeg,SupertablaSemPos,SupertablaUtt_list,Salida,Salida_File,Termns_File);
			} catch (XMLStreamException  e) {
				e.printStackTrace();
			}  catch (FactoryConfigurationError  e) {
				e.printStackTrace();
			}  catch ( FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		   
//		   HashMap<String, HashSet<String>> imagenes_Tabla=new HashMap<String, HashSet<String>>();
//		   HashMap<String,String> TablaSemanticaTexto=new HashMap<String,String>();
//		   
//		   processCollecccion(DocumentosList,DocumentosListText,Supertabla,SupertablaSemPos,TablaSemanticaTexto,imagenes_Tabla,Salida);
			
			return Salida;
		   

	}

	


	
	@Override
	public ArrayList<ImportExportPair> getConfiguracion() {
		if (Parametros==null)
		{
			ArrayList<ImportExportPair> ListaCampos=new ArrayList<ImportExportPair>();
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.File, "List Documents findings strings txt File"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.File, "UMLS output xml File"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.File, "Filter categories txt  File",true));

			Parametros=ListaCampos;
			return ListaCampos;
		}
		else return Parametros;
	}

	@Override
	public String getName() {
		return "Medical Import";
	}

	@Override
	public boolean getCloneLocalFiles() {
		return false;
	}

	

	private void procesaSalida(List<String> Lista, HashMap<String,String> documentosListTextIn,
			HashMap<String, HashMap<String, List<HashMap<String, HashSet<String>>>>> Supertabla, HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>> SupertablaUtt,
			HashMap<String, HashMap<String, HashSet<String>>> SupertablaSemNeg, HashMap<String, HashMap<String, HashSet<String>>> SupertablaSemPos,
			HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>> supertablaUtt_list, CompleteCollectionAndLog salida, String salida_File, String termns_File)
					throws XMLStreamException, FactoryConfigurationError, IOException {
		

		HashMap<String, String> TablaSemanticaTexto = new HashMap<String,String>();
		HashMap<String, String> TablaSemanticaTextoInversa = new HashMap<String,String>();
		HashMap<String,List<String>> TablaSemanticaTextoValidas=new HashMap<String,List<String>>();
		HashMap<String,HashMap<String,String>> Sem_Term_CUI=new HashMap<String,HashMap<String,String>>();
		HashMap<String, List<String>> SupertablaOrden=new HashMap<String, List<String>>();
		
		
//		try {
		try {
		File csvFile = new File(getClass().getResource("Reducido.csv").toURI());
//			String csvFile = getFolder()+"Reducido.csv";
	        String line = "";
	        String cvsSplitBy = ";";

	        
				
			
	        BufferedReader br = new BufferedReader(new FileReader(csvFile));

	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                String[] semsepa = line.split(cvsSplitBy);

	               // System.out.println("Completos=> " + semsepa[0] + " , clave=>" + semsepa[1]);
	                TablaSemanticaTexto.put(semsepa[1], semsepa[0]);
	                TablaSemanticaTextoInversa.put(semsepa[0], semsepa[1]);
	            }
	            br.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
	        HashMap<String, HashSet<String>> Imagenes_List=loadimages();
			

			String csvFile2 = termns_File;
	        String line2 = "";
	        
	        if ((new File(csvFile2).exists()))
			{
	        try {
	        BufferedReader br2 = new BufferedReader(new FileReader(csvFile2));

	        	
	        	String SemanticaA="";
	            while ((line2 = br2.readLine()) != null) {

	            	if (line2.startsWith("Semantica="))
	            		{
	            		SemanticaA=line2.replace("Semantica=>", "");
	            		SemanticaA=TablaSemanticaTextoInversa.get(SemanticaA);
	            		TablaSemanticaTextoValidas.put(SemanticaA, new LinkedList<String>());
	            		}
			    	 else
				    	 {
			    		 
			    		 List<String> Valores = TablaSemanticaTextoValidas.get(SemanticaA);
			    		 if (Valores==null)
			    			 Valores= new LinkedList<String>();
			    		 
			    		 String hijo=line2.trim().replace("++", "");
			    		 String[] hijos=hijo.split("\\[");
			    		 hijo=hijos[0];
			    		 
			    		 Valores.add(hijo);
			    		 
			    		 TablaSemanticaTextoValidas.put(SemanticaA, Valores);
				    	 }
	            }
	            br2.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.err.println("Error con el archvo se calcularan todas");
	            TablaSemanticaTextoValidas=new HashMap<String,List<String>>();
	        }            
			}else
	        	System.out.println("Trabajaremos con todas las gramaticas");
	        
		
		
	        System.out.println("//Procesando Resultado XML P1");
		
	        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

	        
			File file=new File(salida_File);
			//NEW WAY 
			int doc_ind=0;
			
//			File file2=new File("salida.xml");
//			
//			copyFileUsingStream(file,new File("salida.xml"));
//			
			XMLInputFactory instaance = XMLInputFactory.newInstance();
			instaance.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			XMLStreamReader in = instaance.createXMLStreamReader(new FileInputStream(file));
			while (! (in.isEndElement() && in.getLocalName().equals("MMOs"))) {
	            if (in.isStartElement() && in.getLocalName().equals("MMO")) {
	            	doc_ind++;
	            	System.out.println("Documentos->"+doc_ind);
	            	String Doc="unknown";
	            	String Utterance="";
	            	
	            	
	            	
	            	
	            	while(! (in.isEndElement() && in.getLocalName().equals("MMO"))) {
	            		
	            		 if(in.isStartElement() && in.getLocalName().equals("PMID")) {
	            			 Doc=in.getElementText();
	            			 List<String> listaUte=procesaorden(Doc,documentosListTextIn);
	            			 SupertablaOrden.put(Doc, listaUte);
								
	            			 while (! (in.isEndElement() && in.getLocalName().equals("PMID")))
	                			 {
	            				 in.next();
	                			 }
	                       }
	            		 
	            		//ID
	            		 
	            		 
	            		 if(in.isStartElement() && in.getLocalName().equals("UttText")) {
	            			 Utterance=in.getElementText().trim();
	            			 while (! (in.isEndElement() && in.getLocalName().equals("UttText")))
	                			 {
	            				 in.next();
	                			 }
	                       }
	            		 
	            		 //TODO La TextUterance esta a esta altura
	            		
	                    if(in.isStartElement() && in.getLocalName().equals("Candidate")) {
	                    		String CandidatePreferred=null;
	                    		String CandidateCUI=null;
								List<String> MatchedWords=new LinkedList<String>();
								List<String> SemTypes=new LinkedList<String>();
								boolean Negated = false;
								List<String> Posiciones=new LinkedList<String>();
	                    	while (! (in.isEndElement() && in.getLocalName().equals("Candidate")))
	                    	{
	                    		
	                    		if(in.isStartElement() && in.getLocalName().equals("CandidatePreferred")) {
	                    			CandidatePreferred=in.getElementText();
	                   			 while (! (in.isEndElement() && in.getLocalName().equals("CandidatePreferred")))
	                       			 {
	                   				 in.next();
	                       			 }
	                              }
	                    		
	                    		if(in.isStartElement() && in.getLocalName().equals("CandidateCUI")) {
	                    			CandidateCUI=in.getElementText();
	                   			 while (! (in.isEndElement() && in.getLocalName().equals("CandidateCUI")))
	                       			 {
	                   				 in.next();
	                       			 }
	                              }
	                    		
	                    		//PREFFER
	                    		
	                    		
	                    		if(in.isStartElement() && in.getLocalName().equals("MatchedWords")) {
	                    		
	                    		StringBuffer SB=new StringBuffer();
	                    		boolean primero=true;
	                   			 while (! (in.isEndElement() && in.getLocalName().equals("MatchedWords")))
	                   			 	{
	                   				if(in.isStartElement() && in.getLocalName().equals("MatchedWord")) {
	                   					
	                   					if (primero)
											primero=false;
										else
											SB.append("_");
	                   					
	                   					SB.append(in.getElementText());
	                       			 while (! (in.isEndElement() && in.getLocalName().equals("MatchedWord")))
	                           			 {
	                       				 in.next();
	                           			 }
	                                  }
	                   				in.next();
	                   			 	}

	                   			 	 MatchedWords.add(SB.toString());
	                              }

	                    			//MECHED
	                    		
	                    		
	                    		if(in.isStartElement() && in.getLocalName().equals("SemTypes")) {
	                   			 while (! (in.isEndElement() && in.getLocalName().equals("SemTypes")))
	                       			 {
	                   				if(in.isStartElement() && in.getLocalName().equals("SemType")) {
	                   					SemTypes.add(in.getElementText());
	                          			 while (! (in.isEndElement() && in.getLocalName().equals("SemType")))
	                          				 {
	                          				 in.next();
	                          				 }
	                              			 
	                                     }
	                   				
	                   				 in.next();
	                       			 }
	                              }
	                    		
	                    		//MAtCHEDWORDS 
	                    		
	                    		if(in.isStartElement() && in.getLocalName().equals("MatchMaps")) {
	                    			
	                    			
	                      			 while (! (in.isEndElement() && in.getLocalName().equals("MatchMaps")))
	                      				 {
	                      				 //ME FALTA ALGO AQUI
	                      				if(in.isStartElement() && in.getLocalName().equals("MatchMap")) {
	                      					int PositionSTA = -1;
	                      					int PositionEND = -1;
	   	                      			 while (! (in.isEndElement() && in.getLocalName().equals("MatchMap")))
	   	                      				 {
	   	                      				 	
			   	                      			if(in.isStartElement() && in.getLocalName().equals("TextMatchStart")) 
			   	                      				{
			   	                      				try {
			   	                      				PositionSTA=Integer.parseInt(in.getElementText());
													} catch (Exception e) {
														e.printStackTrace();
													}
			   	                      				
				   	                      			 while (! (in.isEndElement() && in.getLocalName().equals("TextMatchStart")))
				   	                      				 {
				   	                      				 in.next();
				   	                      				 }
			   	                      				}
			   	                      			
				   	                      		if(in.isStartElement() && in.getLocalName().equals("TextMatchEnd")) 
			   	                      				{
			   	                      				try {
			   	                      				PositionEND=Integer.parseInt(in.getElementText());
													} catch (Exception e) {
														e.printStackTrace();
													}
			   	                      				
				   	                      			 while (! (in.isEndElement() && in.getLocalName().equals("TextMatchEnd")))
				   	                      				 {
				   	                      				 in.next();
				   	                      				 }
		   	                      				}
				   	                      	
	   	                      				 in.next();
	   	                      				 }
	   	                      			 
	   	                      			 
	   	                      			 if (PositionSTA!=-1&&PositionEND!=-1)
	   	                      			 {
	   	                      				 if (PositionSTA==PositionEND)
	   	                      					 Posiciones.add(Integer.toString(PositionSTA));
	   	                      				 else
	   	                      					 for (int i = PositionSTA; i <= PositionEND; i++) {
	   	                      						 Posiciones.add(Integer.toString(i));
												}
	   	                      			 }
	   	                      			
	   	                          			 
	   	                                 }
	                      				 
	                      				 in.next();
	                      				 }
	                          			 
	                                 }
	                    		
	                    		//NEGATED
	                    		
	                    		if(in.isStartElement() && in.getLocalName().equals("Negated")) {
	                    			String intS=in.getElementText();
	                    			if (intS.equals("1"))
	    								Negated=true;
	                      			 while (! (in.isEndElement() && in.getLocalName().equals("Negated")))
	                      				 {
	                      				 in.next();
	                      				 }
	                          			 
	                                 }
								in.next();
	                    	}
	                    	
	                    	List<String> SemTypesFinal=new LinkedList<String>();
							
							if (!TablaSemanticaTextoValidas.keySet().isEmpty())
							{
							for (String string : SemTypes) {
								if (TablaSemanticaTextoValidas.containsKey(string))
									SemTypesFinal.add(string);
							}
							}else
								SemTypesFinal.addAll(SemTypes);
							
							
							SemTypes=SemTypesFinal;
							
							HashSet<String> ValidCandidate=new HashSet<String>();
							
							
							if (!TablaSemanticaTextoValidas.keySet().isEmpty())
							{
							
							for (String string : SemTypes) {
								List<String> ListaWordVal = TablaSemanticaTextoValidas.get(string);
								
								
								if (ListaWordVal==null||ListaWordVal.isEmpty())
									ValidCandidate.add(string);
								else
									if(ListaWordVal.contains(CandidatePreferred))
										ValidCandidate.add(string);
								

							}
							}else
								ValidCandidate.addAll(SemTypes);
							
							
							if (!SemTypes.isEmpty()&&!MatchedWords.isEmpty()&&!ValidCandidate.isEmpty())
							{
							
							HashMap<String, List<HashMap<String, HashSet<String>>>> ListaSemanticaHsh = Supertabla.get(Doc);
							HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>> ListaSemanticaHshUtt = SupertablaUtt.get(Doc);
							HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>> ListaSemanticaHshUtt_List = supertablaUtt_list.get(Doc);

							
							if (ListaSemanticaHsh==null)
								ListaSemanticaHsh=new HashMap<String, List<HashMap<String, HashSet<String>>>>();
							
							if (ListaSemanticaHshUtt==null)
								ListaSemanticaHshUtt=new HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>>();
							
							if (ListaSemanticaHshUtt_List==null)
								ListaSemanticaHshUtt_List=new HashMap<String, HashMap<String, HashMap<String,HashSet<String>>>>();
							
							
							HashMap<String, HashMap<String, HashSet<String>>> ListaSemanticaHsSem = ListaSemanticaHshUtt.get(Utterance);
							if (ListaSemanticaHsSem==null)
								ListaSemanticaHsSem=new HashMap<String, HashMap<String, HashSet<String>>>();	
							
							HashMap<String, HashMap<String, HashSet<String>>> ListaSemanticaHsSem_list = ListaSemanticaHshUtt_List.get(Utterance);
							if (ListaSemanticaHsSem_list==null)
								ListaSemanticaHsSem_list=new HashMap<String, HashMap<String, HashSet<String>>>();	
							
							for (String sem : SemTypes) {
								
								if (ValidCandidate.contains(sem))
								{
									
								
								List<HashMap<String, HashSet<String>>> SemCan = ListaSemanticaHsh.get(sem);
								if (SemCan==null)
									{
									SemCan=new LinkedList<HashMap<String, HashSet<String>>>();
									SemCan.add(new HashMap<String, HashSet<String>>());
									SemCan.add(new HashMap<String, HashSet<String>>());
									}
								
								HashMap<String, HashSet<String>> SemCanUtt = ListaSemanticaHsSem.get(sem);
								if (SemCanUtt==null)
									SemCanUtt=new HashMap<String, HashSet<String>>();
								
								HashMap<String, HashSet<String>> SemCanUtt_list = ListaSemanticaHsSem_list.get(sem);
								if (SemCanUtt_list==null)
									SemCanUtt_list=new HashMap<String, HashSet<String>>();
								
								
								HashSet<String> ListW;
								
								if (!Negated)
									ListW = SemCan.get(0).get(CandidatePreferred);
								else
									ListW = SemCan.get(1).get(CandidatePreferred);
								
								HashSet<String> ListWUtte=null;
								
								if (!Negated)
									ListWUtte = SemCanUtt.get(CandidatePreferred);
								else
									; 
								
								HashSet<String> ListWUtte_list=null;
								
								if (!Negated)
									ListWUtte_list = SemCanUtt_list.get(CandidatePreferred);
								else
									; 
								
								if (ListW==null)
									ListW=new HashSet<String>();
								
								if (ListWUtte==null)
									ListWUtte=new HashSet<String>();
								
								if (ListWUtte_list==null)
									ListWUtte_list=new HashSet<String>();
								
								for (String mword : MatchedWords) {
									ListW.add(mword);
									ListWUtte.add(mword);
									
								}
								
								for (String posi : Posiciones) {
									ListWUtte_list.add(posi);
								}
								
								if (!Negated)
									SemCan.get(0).put(CandidatePreferred, ListW);
								else
									SemCan.get(1).put(CandidatePreferred, ListW);
								
								if (!Negated)
									SemCanUtt.put(CandidatePreferred, ListWUtte);
								else
									;
								
								if (!Negated)
									SemCanUtt_list.put(CandidatePreferred, ListWUtte_list);
								else
									;
								
								ListaSemanticaHsh.put(sem, SemCan);
								ListaSemanticaHsSem.put(sem, SemCanUtt);
								ListaSemanticaHsSem_list.put(sem, SemCanUtt_list);
								
								if (CandidateCUI!=null)
								{
								HashMap<String, String> Term_CUI = Sem_Term_CUI.get(sem);
								if (Term_CUI==null)
									Term_CUI=new HashMap<String, String>();
								
								Term_CUI.put(CandidatePreferred, CandidateCUI);
								Sem_Term_CUI.put(sem, Term_CUI);
								}
								
								HashMap<String, HashSet<String>> SemCanMiniTabla;
								if (!Negated)
									SemCanMiniTabla = SupertablaSemPos.get(sem);
								else
									SemCanMiniTabla = SupertablaSemNeg.get(sem);
								
								if (SemCanMiniTabla==null)
									SemCanMiniTabla=new HashMap<String, HashSet<String>>();
								
								HashSet<String> ListWMiniTabla = SemCanMiniTabla.get(CandidatePreferred);
								if (ListWMiniTabla==null)
									ListWMiniTabla=new HashSet<String>();
								
								for (String mword : MatchedWords) {
									ListWMiniTabla.add(mword);
								}
								
								SemCanMiniTabla.put(CandidatePreferred, ListWMiniTabla);
								
								if (!Negated)
									SupertablaSemPos.put(sem, SemCanMiniTabla);
								else
									SupertablaSemNeg.put(sem, SemCanMiniTabla);
								}
							}
							
		
							ListaSemanticaHshUtt.put(Utterance, ListaSemanticaHsSem);
							ListaSemanticaHshUtt_List.put(Utterance, ListaSemanticaHsSem_list);
							Supertabla.put(Doc, ListaSemanticaHsh);
							SupertablaUtt.put(Doc, ListaSemanticaHshUtt);
							supertablaUtt_list.put(Doc, ListaSemanticaHshUtt_List);
							
						}
	               			 
	                    	
	                    }
	                    in.next();
	                  }
	            	

	            }
	            in.next();
	        }   
			 
			
			
			
			
			//TODO AQUI HAY QUE GENERAR LOS Documentos
			
			int MaxImages=0;
			for (Entry<String, HashSet<String>> imagenes_doc : Imagenes_List.entrySet()) {
				@SuppressWarnings("unchecked")
				List<String> Imagenes=(List<String>)(List<String>)imagenes_doc.getValue();
				if (Imagenes.size()>MaxImages)
					MaxImages=Imagenes.size();
			}
			
			
			
			int MaxUterancias=0;
			int MaxPos=0;
			int MaxTerm=0;
			int MaxSem=0;
			for (String name : supertablaUtt_list.keySet()){
				HashMap<String, HashMap<String, HashSet<String>>> termino_utterancia_posiciones=new HashMap<>();
				HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> tabla_1 = SupertablaUtt.get(name);
	            HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> tabla_2 = supertablaUtt_list.get(name);
	            
	            
	            
	            HashSet<String> TotalTerms=new HashSet<>();
	            
	            for (String utte_text : tabla_1.keySet()) {
	            	
	            	HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_text = tabla_1.get(utte_text);
	            	HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_count = tabla_2.get(utte_text);
	            	
	            	
	            	
	            	for (String semanticas : semanticas_term_text.keySet()) {
	            		HashMap<String, HashSet<String>> term_text = semanticas_term_text.get(semanticas);
	            		HashMap<String, HashSet<String>> term_count = semanticas_term_count.get(semanticas);
	            		
	            		
	            		TotalTerms.addAll(term_text.keySet());
	            		
	            		for (String termino : term_text.keySet()) {

							HashSet<String> count = term_count.get(termino);
							
							HashMap<String, HashSet<String>> Utterancia_posiciones = termino_utterancia_posiciones.get(termino);
							
							if (Utterancia_posiciones==null)
								Utterancia_posiciones=new HashMap<String, HashSet<String>>();
							
							Utterancia_posiciones.put(utte_text,new HashSet<String>(count));
							
							termino_utterancia_posiciones.put(termino, Utterancia_posiciones);
							
	            			}
	            		
	            		
	            	
	            	}

	            	
	            }
	            
	            if (MaxTerm<TotalTerms.size())
        			MaxTerm=TotalTerms.size();
	            
	            
	            for (Entry<String, HashMap<String, HashSet<String>>> term_ute_pos : termino_utterancia_posiciones.entrySet()) {
					int acum=0;
					for (Entry<String, HashSet<String>> ute_pos : term_ute_pos.getValue().entrySet()) {
						acum=acum+ute_pos.getValue().size();
					}
					
					if (acum>MaxPos)
						MaxPos=acum;
					
				}
	            
			}
			
			for (Entry<String, List<String>> string : SupertablaOrden.entrySet()) {
				if (string.getValue().size()>MaxUterancias)
	            	MaxUterancias=string.getValue().size();
			}
			
			MaxSem=0;
			for (String name : supertablaUtt_list.keySet()){
			  HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> tabla_2 = supertablaUtt_list.get(name);
			  HashMap<String, List<String>> Term_Seman=new HashMap<String, List<String>>();
			 
            for (Entry<String, HashMap<String, HashMap<String, HashSet<String>>>> utter_seman_term_pos_entry : tabla_2.entrySet()) 
				for (Entry<String, HashMap<String, HashSet<String>>> seman_term_pos_entry : utter_seman_term_pos_entry.getValue().entrySet()) 
					for (Entry<String, HashSet<String>> term_pos_entry : seman_term_pos_entry.getValue().entrySet()) {
						List<String> semanticas_term = Term_Seman.get(term_pos_entry.getKey());
						if (semanticas_term==null)
							semanticas_term=new LinkedList<String>();
						
						semanticas_term.add(seman_term_pos_entry.getKey());
						Term_Seman.put(term_pos_entry.getKey(), semanticas_term);
					
					}
            
            for (Entry<String, List<String>> tesrm_seman : Term_Seman.entrySet()) {
            	 if (MaxSem<tesrm_seman.getValue().size())
            		 MaxSem=tesrm_seman.getValue().size();
			}
           
            	
			}
			
			
			
			
			
			CompleteGrammar GramDoc=new CompleteGrammar("Report", "Grammar associated to Reports", salida.getCollection());
			salida.getCollection().getMetamodelGrammar().add(GramDoc);
			
			
			CompleteTextElementType ID=new CompleteTextElementType("Report Id", GramDoc) ;
			ID.setClassOfIterator(ID);
			GramDoc.getSons().add(ID);
			
			List<CompleteTextElementType> ListUtteranceElem=new LinkedList<CompleteTextElementType>();
			
			CompleteTextElementType UtteranceElem=new CompleteTextElementType("Utterances", GramDoc);
			UtteranceElem.setClassOfIterator(UtteranceElem);
			GramDoc.getSons().add(UtteranceElem);
			UtteranceElem.setMultivalued(true);
			
			ListUtteranceElem.add(UtteranceElem);
			
			for (int i = 0; i < MaxUterancias-1; i++) {
				CompleteTextElementType UtteranceElemB=new CompleteTextElementType("Utterances", GramDoc);
				GramDoc.getSons().add(UtteranceElemB);
				UtteranceElemB.setClassOfIterator(UtteranceElem);
				UtteranceElem.setMultivalued(true);
				ListUtteranceElem.add(UtteranceElemB);
			}
			

			List<CompleteResourceElementType> ListImages=new LinkedList<CompleteResourceElementType>();
			

			CompleteResourceElementType ImagesElement=new CompleteResourceElementType("Images", GramDoc);
			ImagesElement.setClassOfIterator(ImagesElement);
			GramDoc.getSons().add(ImagesElement);
			ImagesElement.setMultivalued(true);
			
			ListImages.add(ImagesElement);
			
			for (int i = 0; i < MaxImages-1; i++) {
				CompleteResourceElementType ImagesElementB=new CompleteResourceElementType("Images", GramDoc);
				ImagesElementB.setClassOfIterator(ImagesElement);
				GramDoc.getSons().add(ImagesElementB);
				ImagesElementB.setMultivalued(true);
				ListImages.add(ImagesElementB);
			}
			

			List<CompleteTextElementType> ListTerms=new LinkedList<CompleteTextElementType>();
			HashMap<CompleteTextElementType, List<CompleteTextElementType>> Term_Positions=new HashMap<CompleteTextElementType, List<CompleteTextElementType>>();
			HashMap<CompleteTextElementType, List<CompleteTextElementType>> Term_Semantica=new HashMap<CompleteTextElementType, List<CompleteTextElementType>>();
			
			
			CompleteTextElementType TextElement=new CompleteTextElementType("Term", GramDoc);
			TextElement.setClassOfIterator(TextElement);
			GramDoc.getSons().add(TextElement);
			TextElement.setMultivalued(true);
			ListTerms.add(TextElement);
			
			 List<CompleteTextElementType> Positions= new LinkedList<CompleteTextElementType>();
			
			 CompleteTextElementType PositionElement=new CompleteTextElementType("Position", TextElement,GramDoc);
			 PositionElement.setClassOfIterator(PositionElement);
			 TextElement.getSons().add(PositionElement);
				PositionElement.setMultivalued(true);
				Positions.add(PositionElement);
				
			for (int i = 0; i < MaxPos-1; i++) {
				 CompleteTextElementType PositionElementB=new CompleteTextElementType("Position", TextElement,GramDoc);
				 PositionElementB.setClassOfIterator(PositionElement);
				 TextElement.getSons().add(PositionElementB);
					PositionElementB.setMultivalued(true);
					Positions.add(PositionElementB);
			}
				
			Term_Positions.put(TextElement, Positions);	
			 
			List<CompleteTextElementType> Semanticas= new LinkedList<CompleteTextElementType>();
			 
			 CompleteTextElementType SemanticElement=new CompleteTextElementType("Semantica", TextElement,GramDoc);
			 SemanticElement.setClassOfIterator(SemanticElement);
			 TextElement.getSons().add(SemanticElement);
			 SemanticElement.setMultivalued(true);
			 Semanticas.add(SemanticElement);
			 	
			 CompleteTextElementType CUIElement=new CompleteTextElementType("CUI", TextElement,GramDoc);
			 CUIElement.setClassOfIterator(CUIElement);
			 SemanticElement.getSons().add(CUIElement);
			 
			 
			 for (int i = 0; i < MaxSem-1; i++) {
				 CompleteTextElementType SemanticElement1=new CompleteTextElementType("Semantica", TextElement,GramDoc);
				 SemanticElement1.setClassOfIterator(SemanticElement);
				 TextElement.getSons().add(SemanticElement1);
				 SemanticElement1.setMultivalued(true);
				 Semanticas.add(SemanticElement1);
				 
				 CompleteTextElementType CUIElement1=new CompleteTextElementType("CUI", TextElement,GramDoc);
				 CUIElement1.setClassOfIterator(CUIElement);
				 SemanticElement1.getSons().add(CUIElement1);
				 
			 }
			 
			
			 Term_Semantica.put(TextElement, Semanticas);	
			 
			for (int i = 0; i < MaxTerm-1; i++) {
				CompleteTextElementType TextElementB=new CompleteTextElementType("Term", GramDoc);
				TextElementB.setClassOfIterator(TextElement);
				GramDoc.getSons().add(TextElementB);
				TextElementB.setMultivalued(true);
				ListTerms.add(TextElementB);
				
				
				 List<CompleteTextElementType> PositionsB= new LinkedList<CompleteTextElementType>();
					
					for (int j = 0; j < MaxPos; j++) {
						 CompleteTextElementType PositionElementC=new CompleteTextElementType("Position", TextElementB,GramDoc);
						 PositionElementC.setClassOfIterator(PositionElement);
						 TextElementB.getSons().add(PositionElementC);
							PositionElementC.setMultivalued(true);
							PositionsB.add(PositionElementC);
					}
						
					Term_Positions.put(TextElementB, PositionsB);		
					
					
					List<CompleteTextElementType> SemanticasB= new LinkedList<CompleteTextElementType>();
					 
					 
					 for (int j = 0; j < MaxPos; j++) {
						 CompleteTextElementType SemanticElementC=new CompleteTextElementType("Semantica", TextElementB,GramDoc);
						 SemanticElementC.setClassOfIterator(SemanticElement);
						 TextElementB.getSons().add(SemanticElementC);
						 SemanticElementC.setMultivalued(true);
						 SemanticasB.add(SemanticElementC);
						 
						 CompleteTextElementType CUIElement1=new CompleteTextElementType("CUI", TextElement,GramDoc);
						 CUIElement1.setClassOfIterator(CUIElement);
						 SemanticElementC.getSons().add(CUIElement1);
						 
					 }
					 
					
					 Term_Semantica.put(TextElementB, SemanticasB);	
					
				
			}
			
			
			
			
			
			
			
			if (consoleDebug)
				{
				System.out.println(MaxImages+"_I-"+ListImages.size() );
				System.out.println(MaxUterancias+"_U-"+ListUtteranceElem.size());
				
				System.out.println(MaxTerm+"_T-"+ListTerms.size() );
				
				for (Entry<CompleteTextElementType, List<CompleteTextElementType>> elem_pos : Term_Positions.entrySet()) 
					System.out.println(MaxPos+"_P-"+elem_pos.getValue().size() );
				
				
				
				}
			
			
			for (String name : supertablaUtt_list.keySet()){
				
				@SuppressWarnings("unchecked")
				List<String> Imagenes=(List<String>)(List<String>)Imagenes_List.get(name);
				
				String icon="";
				if (Imagenes!=null&&!Imagenes.isEmpty())
					icon=Imagenes.get(0);
				
				 String Texto_general = documentosListTextIn.get(name);
				CompleteDocuments CD=new CompleteDocuments(salida.getCollection(), name+"//"+Texto_general, icon);
				salida.getCollection().getEstructuras().add(CD);
				
				CompleteTextElement IDElem=new CompleteTextElement(ID, name);
				CD.getDescription().add(IDElem);
				
				for (int i = 0; i < Imagenes.size(); i++) {
					CompleteResourceElementURL RU=new CompleteResourceElementURL(ListImages.get(i), Imagenes.get(i));
					CD.getDescription().add(RU);
				}
				

//				HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> tabla_1 = SupertablaUtt.get(name);
				
	            HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> tabla_2 = supertablaUtt_list.get(name);
	           	            
	            
	            HashMap<String, Integer> uterancia_nTerms=new HashMap<String, Integer>();
	            
	            List<String> UtterancesOrdenadas=new LinkedList<String>(SupertablaOrden.get(name));
	            
	            
	            
	            int acumulado=0;
	            for (int i = 0; i < UtterancesOrdenadas.size(); i++) {
	            	CompleteTextElement UteElem=new CompleteTextElement(ListUtteranceElem.get(i), UtterancesOrdenadas.get(i));
					CD.getDescription().add(UteElem);	
					
					
					List<String> TokenWords_frase=new LinkedList<String>();
					String frase=UtterancesOrdenadas.get(i);
//					String fraseSucia=new String(frase.trim()+".");
					frase=frase.trim();
					frase=frase.replace(",", ", ");
					frase=frase.replace(" ,", ",");
					frase=frase.replace(";", "; ");
					frase=frase.replace(" ;", ";");
					frase=frase.replace("/", "/ ");
					frase=frase.replace(" /", "/");
					frase=frase.replace("-", "- ");
					frase=frase.replace(" -", "-");
					frase=frase.replace("  ", " ");
						String[] palabras = frase.split(" ");
						for (String palabra : palabras) {
							if (!palabra.trim().isEmpty())
							{
							TokenWords_frase.add(palabra);
							}
						}
					frase=frase+".";
					
					uterancia_nTerms.put(UtterancesOrdenadas.get(i),acumulado);
					acumulado=acumulado+TokenWords_frase.size();
					
					
				}
	            
	            HashMap<String, HashSet<Integer>> Term_pos=new HashMap<String, HashSet<Integer>>();
	            HashMap<String, List<String>> Term_Seman=new HashMap<String, List<String>>();
	            
	            for (Entry<String, HashMap<String, HashMap<String, HashSet<String>>>> utter_seman_term_pos_entry : tabla_2.entrySet()) 
					for (Entry<String, HashMap<String, HashSet<String>>> seman_term_pos_entry : utter_seman_term_pos_entry.getValue().entrySet()) 
						for (Entry<String, HashSet<String>> term_pos_entry : seman_term_pos_entry.getValue().entrySet()) {
							List<String> semanticas_term = Term_Seman.get(term_pos_entry.getKey());
							if (semanticas_term==null)
								semanticas_term=new LinkedList<String>();
							
							semanticas_term.add(seman_term_pos_entry.getKey());
							Term_Seman.put(term_pos_entry.getKey(), semanticas_term);
							
							HashSet<Integer> positions_term = Term_pos.get(term_pos_entry.getKey());
							
							if (positions_term==null)
								positions_term=new HashSet<Integer>(); 
							
							
							for (String inetegerS : term_pos_entry.getValue()) {
								int rela=uterancia_nTerms.get(utter_seman_term_pos_entry.getKey());
								try {
									rela=rela+Integer.parseInt(inetegerS);
								} catch (Exception e) {
									e.printStackTrace();
								}
								positions_term.add(rela);		
							}
							
							Term_pos.put(term_pos_entry.getKey(), positions_term);
							
						}
					
	            	
	           List<String> ListTermsFin = new LinkedList<String>(Term_pos.keySet());	
	           for (int j = 0; j < ListTermsFin.size(); j++) {
				String term = ListTermsFin.get(j);
				List<Integer> Posiciones = new LinkedList<>(Term_pos.get(term));
					List<String> SemanticasBis = Term_Seman.get(term);
					
					List<CompleteTextElementType> SoucionSem = Term_Semantica.get(ListTerms.get(j));
					
					for (int i = 0; i < SemanticasBis.size(); i++){
						String semanticas = SemanticasBis.get(i);
						CompleteTextElement PosElemElem=new CompleteTextElement(SoucionSem.get(i), semanticas);
						CD.getDescription().add(PosElemElem);
						
						if (Sem_Term_CUI.get(semanticas)!=null&&
							Sem_Term_CUI.get(semanticas).get(term)!=null)
					{
								
								CompleteTextElement PosElemElemCUI=new CompleteTextElement((CompleteTextElementType) SoucionSem.get(i).getSons().get(0), Sem_Term_CUI.get(semanticas).get(term));
								CD.getDescription().add(PosElemElemCUI);
							
							
							
							//HABRA QUE GENERAR SEMANTICAS POR TERMINO Y LUEGO METERSELAS
//						String CUIValue = Sem_Term_CUI.get(semanticas).get(termino);
//						Text cui_string = document.createTextNode(CUIValue);
//						cuiType.appendChild(cui_string);
					}
					}
					
					
					CompleteTextElement TermeElem=new CompleteTextElement(ListTerms.get(j), term);
					CD.getDescription().add(TermeElem);
					
					List<CompleteTextElementType> Soucion = Term_Positions.get(ListTerms.get(j));
					
					for (int i = 0; i < Posiciones.size(); i++) {
						CompleteTextElement PosElemElem=new CompleteTextElement(Soucion.get(i), Integer.toString(Posiciones.get(i)));
						CD.getDescription().add(PosElemElem);
					}
					
					
				}
	            
	            
	            
	            
	            /*
	             * 
	            
	            
	            int acumulado=0;
	            for (String utte_text : UtterancesOrdenadas) {
	            	HashMap<String, HashMap<String, HashSet<String>>> semantica_termino_pos = tabla_2.get(utte_text);
	            	
	            	HashMap<String, HashMap<String, HashSet<String>>> termino_semantica_pos = new HashMap<>();
	            	
	            	for (Entry<String, HashMap<String, HashSet<String>>> seman_term_pos_entry : semantica_termino_pos.entrySet()) {
						for (Entry<String, HashSet<String>> term_pos : seman_term_pos_entry.getValue().entrySet()) {
							HashMap<String, HashSet<String>> Semanticas = termino_semantica_pos.get(term_pos.getKey());
							if (Semanticas==null)
								Semanticas=new HashMap<String, HashSet<String>>();
							
							Semanticas.put(seman_term_pos_entry.getKey(), term_pos.getValue());
							termino_semantica_pos.put(term_pos.getKey(), Semanticas);
						}
	            		
					}
	            	
	            	
	            	
	            	
	            	
	            	
	            }
	            
	            
	            
	            
	            
	            
	          	
	            
	            List<String> Terminos=new LinkedList<>(termino_semanticas.keySet());
	            
	            for (int i = 0; i < Terminos.size(); i++) {
	            	String termino = Terminos.get(i);
	            	
	            	CompleteTextElement TermeElem=new CompleteTextElement(ListTerms.get(i), termino);
					CD.getDescription().add(TermeElem);
					
					HashMap<String, HashSet<String>> uter_pos = termino_utterancia_posiciones.get(termino);
					
					int acumulado=0;
					for (int j = 0; j < utteran.size(); j++) {
						String uteranciaIndi=utteran.get(j);
						List<String> Posicionestermino = new LinkedList<>(uter_pos.get(uteranciaIndi));
						if (Posicionestermino!=null)
						{
							List<CompleteTextElementType> PosElements = Term_Positions.get(ListTerms.get(i));
							for (int k = 0; k < Posicionestermino.size(); k++) {
								String posiciones = Posicionestermino.get(k);
								int PosicionRel=0;
								try {
									PosicionRel=Integer.parseInt(posiciones);
									
								} catch (Exception e) {
									// TODO: handle exception
								}
								PosicionRel=PosicionRel+acumulado;
								CompleteTextElement PosElemElem=new CompleteTextElement(PosElements.get(k), Integer.toString(PosicionRel));
								CD.getDescription().add(PosElemElem);
							}
						}
						
						acumulado=acumulado+uterancia_nTerms.get(uteranciaIndi);
						
					}
					
	            	
//	            	List<String> Semanticas = termino_semanticas.get(Termino);
//	            	termino_utterancia_posiciones
					
					
					
				}
	            
	            
	            
	            
					List<String> TokenWords_frase=new LinkedList<String>();
					String frase=utteran.get(i);
					String fraseSucia=new String(frase.trim()+".");
					frase=frase.trim();
					frase=frase.replace(",", ", ");
					frase=frase.replace(" ,", ",");
					frase=frase.replace(";", "; ");
					frase=frase.replace(" ;", ";");
					frase=frase.replace("/", "/ ");
					frase=frase.replace(" /", "/");
					frase=frase.replace("-", "- ");
					frase=frase.replace(" -", "-");
					frase=frase.replace("  ", " ");
						String[] palabras = frase.split(" ");
						for (String palabra : palabras) {
							if (!palabra.trim().isEmpty())
							{
							TokenWords_frase.add(palabra);
							}
						}
					frase=frase+".";

					
					HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_text = tabla_1.get(utteran.get(i));
	            	HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_count = tabla_2.get(utteran.get(i));
	            	
	            	for (String semanticas : semanticas_term_text.keySet()) {
	            		HashMap<String, HashSet<String>> term_text = semanticas_term_text.get(semanticas);
	            		HashMap<String, HashSet<String>> term_count = semanticas_term_count.get(semanticas);
	            		
	            		
	            		List<String> terminosValidos=new LinkedList<String>(term_text.keySet());
	            		
	            		
	            		for (int j = 0; j < terminosValidos.size(); j++) {
							String termino = terminosValidos.get(j);
							
							CompleteTextElement TermeElem=new CompleteTextElement(ListTerms.get(j), termino);
							CD.getDescription().add(TermeElem);
							
							
//							HashSet<String> text = term_text.get(termino);
							List<String> count = new LinkedList<String>(term_count.get(termino));
							
							List<CompleteTextElementType> Soucion = Term_Positions.get(termino);
							
							for (int k = 0; k < count.size(); k++) {
								CompleteTextElement PosElem=new CompleteTextElement(Soucion.get(k), count.get(k));
								CD.getDescription().add(PosElem);
							}
							
							
							
	            			}
	            	
	            	}
	             * 
	             * */
	            
	          //  Ya tengo las utterancias
	            
	            /**
	            for (String utte_text : tabla_1.keySet()) {
	            	
	            	HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_text = tabla_1.get(utte_text);
	            	HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_count = tabla_2.get(utte_text);
	            	
	            	for (String semanticas : semanticas_term_text.keySet()) {
	            		HashMap<String, HashSet<String>> term_text = semanticas_term_text.get(semanticas);
	            		HashMap<String, HashSet<String>> term_count = semanticas_term_count.get(semanticas);
	            		
	            		if (term_text.keySet().size()>MaxTerm)
	            			MaxTerm=term_text.keySet().size();
	            		
	            		for (String termino : term_text.keySet()) {
	            			//AQUI NO SE USA
//							HashSet<String> text = term_text.get(termino);
							HashSet<String> count = term_count.get(termino);
							
							if (count.size()>MaxPos)
								MaxPos=count.size();
							
	            			}
	            	
	            	}
	            }
	            
	*/
			}
			
			//AQUI YA YA
			
			
			/**
			
			
			
			for (String name : supertablaUtt_list.keySet()) {
				@SuppressWarnings("unchecked")
				List<String> Imagenes=(List<String>)Imagenes_List.get(name);
				String Texto_general = documentosListTextIn.get(name);
				
				String icon=null;
				
				if (Imagenes!=null&&!Imagenes.isEmpty())
					icon=Imagenes.get(0);
				
				CompleteDocuments CD=new CompleteDocuments(salida.getCollection(), Texto_general, icon);
				salida.getCollection().getEstructuras().add(CD);
				
			}
			
			
			 try {
				//TODO AQUI TELA MARINELA
					for (String name : supertablaUtt_list.keySet()) {
						
						@SuppressWarnings("unchecked")
						List<String> Imagenes=(List<String>)Imagenes_List.get(name);
						
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			            DocumentBuilder builder;

							builder = factory.newDocumentBuilder();

			            DOMImplementation implementation = builder.getDOMImplementation();
			            Document document = implementation.createDocument(null, name, null);
			            document.setXmlVersion(XML_VERSION);
			            
			            
			            Element raiz = document.getDocumentElement();
			            

			            String Texto_general = documentosListTextIn.get(name);
			            Element texto_value = document.createElement("Texto"); 
			            Text text_string = document.createTextNode(Texto_general);
			            texto_value.appendChild(text_string);
			            raiz.appendChild(texto_value);

			            Element imagenes = document.createElement("Imagenes"); 
			            raiz.appendChild(imagenes);
			           
			            if (Imagenes!=null)
				            for (String imagenURL : Imagenes) {
				            	Element imagen = document.createElement("Imagen"); 
					            imagenes.appendChild(imagen);
					            Text phrase_value = document.createTextNode(imagenURL);
					            imagen.appendChild(phrase_value);
							}
			            
			            
			            
			            HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> tabla_1 = SupertablaUtt.get(name);
			            HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> tabla_2 = supertablaUtt_list.get(name);
			            
			            
			            Element uterances = document.createElement("Utterances"); 
			            raiz.appendChild(uterances);
			            
			            for (String utte_text : tabla_1.keySet()) {
			            	HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_text = tabla_1.get(utte_text);
			            	HashMap<String, HashMap<String, HashSet<String>>> semanticas_term_count = tabla_2.get(utte_text);
			            	
			            	Element uterance = document.createElement("Utterance"); 
			            	uterances.appendChild(uterance);
			            	
			            	Element phrase = document.createElement("PhraseText"); 
			            	uterance.appendChild(phrase);
			            	Text phrase_value = document.createTextNode(utte_text);
			            	phrase.appendChild(phrase_value);
			            	
			            	
			            	Element terms = document.createElement("Terms"); 
			            	uterance.appendChild(terms);
			            	
			            	
			            	
			            	
			            	
			            	for (String semanticas : semanticas_term_text.keySet()) {
			            		HashMap<String, HashSet<String>> term_text = semanticas_term_text.get(semanticas);
			            		HashMap<String, HashSet<String>> term_count = semanticas_term_count.get(semanticas);
			            		
								for (String termino : term_text.keySet()) {
									HashSet<String> text = term_text.get(termino);
									HashSet<String> count = term_count.get(termino);
									
									Element term = document.createElement("Term"); 
									terms.appendChild(term);
									
									Element semType = document.createElement("SemType"); 
									term.appendChild(semType);
									String SenamticaLimpia = "no semantic type associated";
									if (TablaSemanticaTexto.get(semanticas)!=null)
										SenamticaLimpia=TablaSemanticaTexto.get(semanticas);
									Text semType_string = document.createTextNode(SenamticaLimpia);
									semType.appendChild(semType_string);
									
									Element cuiType = document.createElement("CUI"); 
									term.appendChild(cuiType);
									
									if (Sem_Term_CUI.get(semanticas)!=null&&
											Sem_Term_CUI.get(semanticas).get(termino)!=null)
									{
										String CUIValue = Sem_Term_CUI.get(semanticas).get(termino);
										Text cui_string = document.createTextNode(CUIValue);
										cuiType.appendChild(cui_string);
									}
									

									Element term_value = document.createElement("TermValue"); 
									term.appendChild(term_value);
									Text term_value_string = document.createTextNode(termino);
									term_value.appendChild(term_value_string);
									
									Element words = document.createElement("Words"); 
									term.appendChild(words);
									
									for (String word_value : text) {
										Element word = document.createElement("Word"); 
										words.appendChild(word);
										Text word_value_string = document.createTextNode(word_value);
										word.appendChild(word_value_string);
									}
									
									Element positions = document.createElement("Positions"); 
									term.appendChild(positions);
									
									for (String positions_value : count) {
										Element position = document.createElement("Position"); 
										positions.appendChild(position);
										Text word_value_string = document.createTextNode(positions_value);
										position.appendChild(word_value_string);
									}
									
									
									
								}
							}
			            	
			            	
			            	
						}
            
			            //Generate XML
			            Source source = new DOMSource(document);
			            //Indicamos donde lo queremos almacenar
			            Result result = new StreamResult(new java.io.File(getFolder()+DOCUMENTOS+File.separator+name+".xml")); //nombre del archivo
			            Transformer transformer = TransformerFactory.newInstance().newTransformer();
			            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			            transformer.transform(source, result);
					}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			
			*/
			
		
		
	}
	
	

	private LinkedList<String> procesaorden(String doc, HashMap<String, String> documentosListTextIn) {
		String Text=documentosListTextIn.get(doc);
		String[] seperado=Text.split("\\.");
		LinkedList<String> Salida= new LinkedList<String>();
		for (String string : seperado) {
			if (!string.trim().isEmpty())
				Salida.add(string.trim()+".");
		} 
		return Salida;
	}


	private static void LoadSampleTXT(List<String> documentosList, HashMap<String,String> documentosListText, String filenamesample) throws FileNotFoundException {
		
	    String line = "";
	    
	    try{
	    	
	    BufferedReader br = new BufferedReader(new FileReader(filenamesample));

	        while ((line = br.readLine()) != null) {

	        	if (!line.isEmpty())
	        		{
	        		String[] LineT=line.split("\\|");
	        		StringBuffer Texto=new StringBuffer();
	        		String NombreArhivo = LineT[0];
	        		documentosList.add(NombreArhivo);
	        		for (int i = 1; i < LineT.length; i++) {
	        			if (i!=1)
	        				Texto.append("\\|");
	        			Texto.append(LineT[i]);
					}
	        		
	        		documentosListText.put(NombreArhivo,Texto.toString());
	        		}
	        }
	     br.close();

	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error en la lectura del sample");
	    }
		
	    if (documentosList.size()!=documentosListText.size())
	    	throw new RuntimeException("Error en la lectura del sample");
	    
	}
	

	private HashMap<String, HashSet<String>> loadimages() {
		JsonReader reader;
		
		try {
		File jsonfile = new File(getClass().getResource("openi_nlm_nih_gov.json").toURI());
		
		
			reader = new JsonReader(new FileReader(jsonfile));
			Gson gson = new Gson();
			HashMap<String, HashSet<String>> Imagenes_List =  gson.fromJson(reader, HashMap.class);
			
			if (consoleDebug)
				System.out.println("Cargada");
			
			return Imagenes_List;
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();

		} catch (URISyntaxException e) {
			e.printStackTrace();
			
		}
		
		return new HashMap<String, HashSet<String>>();
		}

}
