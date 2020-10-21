package spbu.lucas.mbele;

import java.util.ArrayList;

import spbu.lucas.mbele.Lexer.TokenType;

public class BuildAstTree {

	
	private ArrayList<Token> tokens;
	private boolean pass = true;
	private char ch ='"';
	private String dot_header = "digraph \"DirectedGraph\" {\n";
	private String dot_body;
    private String dot_footer ="}";
    private int countNode = 1;
    private final int save_countNodeFirstLevel =1;
    private int save_countNodeSecondLevel;
	private String value="";
	public BuildAstTree(ArrayList <Token> tokens)
	{
		this.tokens = tokens;
		this.dot_header+= "node[shape=none,fontsize=12,height =.1];\n";
		this.dot_header+= "rankstep =.3;\n";
		this.dot_header+="edge[arrowsize=.5]";
		this.dot_body="node1[label=\"program\"]\n ";
	}
	
	public String beta_try ()
	{
		
		for(int i =0;i<this.tokens.size();i++)
		{
			//Verifier le type de variable est PRIMORDIAL
			if (this.tokens.get(i).type == TokenType.TYPEVALUES)
			{
				//On avance de deux pas afin de savoir si on a affaire a une variable ou une fonction
				i = i+2;
				//On supposse aue si on a un un parenthese ouverte alors c'est une fonction
				if (this.tokens.get(i).type == TokenType.PAREN)
				{
					//Du coup c'est une fonction
					
					countNode++;
					//Oui on recule d'un pas pour noter le nom de la variable (Tu sais, reculer pour mieux sauter XD)
					i =i-1;
					int typevalue = i -1;
					//On affiche le nom de la fonction
					this.dot_body+="node"+countNode+"[label="+ch+tokens.get(typevalue).data+" "+tokens.get(i).data+ "(function)"+ch+"] \n";
					//La on lie ce noyau  a notre programme 
					this.dot_body+= "node"+save_countNodeFirstLevel+" -> node"+countNode+"\n";
					//Cette variable permet de lier tous les autres noyaux a cette fonction
					int savecountNodeFunction = countNode;
					i++;
					String s_value_="";
					while(!this.tokens.get(i).data.equals("{"))
					{
						//Si la fonction comporte des parametres ou pas
						s_value_+= tokens.get(i).data+" ";
						i++;
					}
					countNode++;
					this.dot_body+="node"+countNode+"[label="+ch+s_value_+ch+"] \n";
					this.dot_body+= "node"+savecountNodeFunction+" -> node"+countNode+"\n";

					countNode++;
					int savecountNodeBlock = countNode;
					//On a fini de noter le nom de la fonction, et ses parametres si elle en a bien sur
					//Maintenant on rentre dans la fonction
					//On va jusqu'au bout de la fonction
					while(!this.tokens.get(i).data.equals("}"))
					{
						//Là c'est juste a titre esthetique que nous creeons ce noyau "block" afin de rendre l'abre plus concis 
						//Du coup les branches qui vont suivre seront liees a ce block
						//Voila pourquoi on cree cette variable savecountNodeBlock
						if (tokens.get(i).data.equals("{") )
						{
							//On y rentre de toute facon
						this.dot_body+="node"+countNode+"[label="+ch+"(block)"+ch+"] \n";
						this.dot_body+= "node"+savecountNodeFunction+" -> node"+countNode+"\n";

						}
						//La c'est evident , je crois
						if(tokens.get(i).type == TokenType.ASSIGNEMENT)
						{
							//Cette condition nous amenera dans tous les cas a verifier a gauche et a droite
							countNode++;
							this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
							this.dot_body+= "node"+savecountNodeBlock+" -> node"+countNode+"\n";
							//On recule d'un pas afin de noter le nom de la  variable
							      i--;
							      //Au cas ou il y a une erreur qui surgit, mais c'est fort peu probable
						     if (i <0)
							   {
								  break;
						       }

							else
							{
								String s_value ="";
								String type ="";
								String type_value ="";
								if(this.tokens.get(i).type == TokenType.TAB)
								{
									s_value = ""+tokens.get(i).data;
									i--;
								}
								if (this.tokens.get(i).type ==TokenType.VARIABLE) {
									if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
										type= this.tokens.get(i - 1).data;
									}
									type_value = type+" "+tokens.get(i).data;
								}
								save_countNodeSecondLevel = countNode;
								countNode++;
								this.dot_body+="node"+countNode+"[label="+ch+type_value+s_value+ch+"] \n";
								this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";


								i+=2;
							}
							String s_value = "";
							while(!this.tokens.get(i).data.equals(";")) {
								s_value = tokens.get(i).data+"";
								i++;
							}
							countNode++;
										this.dot_body+="node"+countNode+"[label="+ch+s_value+ch+"] \n";
										this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";

						}
                        //1er mot cle decouvert
						if (this.tokens.get(i).type == TokenType.KEYWORDS)
						{
							countNode++;
							this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
							this.dot_body+= "node"+savecountNodeBlock+" -> node"+countNode+"\n";
							int savecountNodeBlock2 = countNode;
				
							//On cherche les conditions
							String s="";
							while(!tokens.get(i).data.equals(")"))
								{
								    s+= tokens.get(i+1).data+" ";
									i++;
								}
							countNode++;
							this.dot_body+="node"+countNode+"[label="+ch+s+ch+"] \n";
							this.dot_body+= "node"+savecountNodeBlock2+" -> node"+countNode+"\n";

						    countNode++;
							int save_countNodeKeywords = countNode;	//AFIN DE LIER AU BLOCK
							  while (!tokens.get(i).data.equals("}"))
							  {

								if (tokens.get(i).data.equals("{") )
								{
								this.dot_body+="node"+countNode+"[label="+ch+"block"+ch+"] \n";
								this.dot_body+= "node"+savecountNodeBlock2+" -> node"+countNode+"\n";

								}

								if(tokens.get(i).type == TokenType.ASSIGNEMENT)
								{

									countNode++;
									this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
									this.dot_body+= "node"+save_countNodeKeywords+" -> node"+countNode+"\n";
									//On recule d'un pas
									      i--;
								     if (i <0)
									   {
										  break;
								       }

									else
									{
										String s_value ="";
										String type ="";
										String type_value ="";
										if(this.tokens.get(i).type == TokenType.TAB)
										{
											s_value = ""+tokens.get(i).data;
											i--;
											if (this.tokens.get(i).type ==TokenType.VARIABLE) {
												if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
													type = this.tokens.get(i - 1).data;
												}
												type_value = type + " " + tokens.get(i).data + s_value;
												i++;
											}
										}
										if (this.tokens.get(i).type ==TokenType.VARIABLE) {
											if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
												type= this.tokens.get(i - 1).data;
											}
											type_value = type+" "+tokens.get(i).data+s_value;
										}
										save_countNodeSecondLevel = countNode;
										countNode++;
										this.dot_body+="node"+countNode+"[label="+ch+type_value+ch+"] \n";
										this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";

										i+=2;
										s_value = "";
										while(!this.tokens.get(i).data.equals(";")) {
											s_value += tokens.get(i).data+"";
											i++;
										}
										countNode++;
										this.dot_body+="node"+countNode+"[label="+ch+s_value+ch+"] \n";
										this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";

									}

								}
								//On trouve un deuxieme cle
								if (this.tokens.get(i).type == TokenType.KEYWORDS)
								{
									countNode++;
									this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
									this.dot_body+= "node"+save_countNodeKeywords+" -> node"+countNode+"\n";
									int  save_countNodeBlock3 = countNode;

									String s2="";
									while(!tokens.get(i).data.equals(")"))
									{
										s2+= tokens.get(i+1).data+" ";
										i++;
									}
									countNode++;
									this.dot_body+="node"+countNode+"[label="+ch+s2+ch+"] \n";
									this.dot_body+= "node"+save_countNodeBlock3+" -> node"+countNode+"\n";
									  countNode++;	
									  int  save_countNodeKeywords2= countNode;
									  while (!tokens.get(i).data.equals("}"))
									  {
										  if (tokens.get(i).data.equals("{") )
										  {
											  this.dot_body+="node"+countNode+"[label="+ch+"block"+ch+"] \n";
											  this.dot_body+= "node"+save_countNodeBlock3+" -> node"+countNode+"\n";

										  }
										if(tokens.get(i).type == TokenType.ASSIGNEMENT)
										{
											countNode++;
											this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
											this.dot_body+= "node"+save_countNodeKeywords2+" -> node"+countNode+"\n";

											//On recule d'un pas
											      i--;
										     if (i <0)
												  break;
											else
											{
												String s_value ="";
												String type ="";
												String type_value ="";
												if(this.tokens.get(i).type == TokenType.TAB)
												{
													s_value = ""+tokens.get(i).data;
													i--;
													if (this.tokens.get(i).type ==TokenType.VARIABLE) {
														if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
															type = this.tokens.get(i - 1).data;
														}
														type_value = type + " " + tokens.get(i).data + s_value;
														i++;
													}
												}
												if (this.tokens.get(i).type ==TokenType.VARIABLE) {
													if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
														type= this.tokens.get(i - 1).data;
													}
													type_value = type+" "+tokens.get(i).data+s_value;
												}
												save_countNodeSecondLevel = countNode;
												countNode++;
												this.dot_body+="node"+countNode+"[label="+ch+type_value+ch+"] \n";
												this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";
												i+=2;
												s = "";
												while(!this.tokens.get(i).data.equals(";")) {
													s = tokens.get(i).data+"";
													i++;
												}
												countNode++;
												this.dot_body+="node"+countNode+"[label="+ch+s+ch+"] \n";
												this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";

											}
										}
										i++;
									 }
								}
								i++;
							 }

						}

						i++;
					}
			}


				//LA FONCTION C'EST EN HAUT
				
				// UNE VARIABLE NORMALE QUOI!!!



			if (this.tokens.get(i).type == TokenType.ASSIGNEMENT || this.tokens.get(i).type==TokenType.TAB)
			{
				countNode++;
				if (this.tokens.get(i).type == TokenType.TAB)
					i++;
				this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
				this.dot_body+= "node"+save_countNodeFirstLevel+" -> node"+countNode+"\n";
				//ON RECULE D'UN PAS

				i--;

				if (i <0)
			        break;
				else
				{
					String s_value ="";
					String type ="";
					String type_value ="";
					if(this.tokens.get(i).type == TokenType.TAB)
					{
						s_value = ""+tokens.get(i).data;
						i--;
						if (this.tokens.get(i).type ==TokenType.VARIABLE) {
							if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
								type = this.tokens.get(i - 1).data;
							}
							type_value = type + " " + tokens.get(i).data + s_value;
							i++;
						}
					}
					if (this.tokens.get(i).type ==TokenType.VARIABLE) {
						if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
							type= this.tokens.get(i - 1).data;
						}
						type_value = type+" "+tokens.get(i).data+s_value;
					}
					save_countNodeSecondLevel = countNode;
					countNode++;
					this.dot_body+="node"+countNode+"[label="+ch+type_value+ch+"] \n";
					this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";
					i+=2;
					String s = "";
					while(!this.tokens.get(i).data.equals(";")) {
						s += tokens.get(i).data+"";
						i++;
					}
					countNode++;
					this.dot_body+="node"+countNode+"[label="+ch+s+ch+"] \n";
					this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";

				}
			}

	}
			//Dans le cas ou on rencontre direct un keyword
			
		if (this.tokens.get(i).type == TokenType.KEYWORDS)
			{
				countNode++;
				this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
				this.dot_body+= "node"+save_countNodeFirstLevel+" -> node"+countNode+"\n";

				int save_countNodeBlock = countNode;
				String s2="";
				while(!tokens.get(i).data.equals(")"))
				{
					s2+= tokens.get(i+1).data+" ";
					i++;
				}
				countNode++;
				this.dot_body+="node"+countNode+"[label="+ch+s2+ch+"] \n";
				this.dot_body+= "node"+save_countNodeBlock+" -> node"+countNode+"\n";
				  countNode++;	
				  int  save_countNodeKeywords= countNode;
				  while (!tokens.get(i).data.equals("}"))
				  {
					  if (tokens.get(i).data.equals("{") )
					  {
						  this.dot_body+="node"+countNode+"[label="+ch+"block"+ch+"] \n";
						  this.dot_body+= "node"+save_countNodeBlock+" -> node"+countNode+"\n";

					  }
					if(tokens.get(i).type == TokenType.ASSIGNEMENT)
					{
						countNode++;
						this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
						this.dot_body+= "node"+save_countNodeKeywords+" -> node"+countNode+"\n";

						//On recule d'un pas
						      i--;
					     if (i <0)
						   {
							  break;
					       }
						else
						{
							String s_value ="";
							String type ="";
							String type_value ="";
							if(this.tokens.get(i).type == TokenType.TAB)
							{
								s_value = ""+tokens.get(i).data;
								i--;
								if (this.tokens.get(i).type ==TokenType.VARIABLE) {
									if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
										type = this.tokens.get(i - 1).data;
									}
									type_value = type + " " + tokens.get(i).data + s_value;
									i++;
								}
							}
							if (this.tokens.get(i).type ==TokenType.VARIABLE) {
								if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
									type= this.tokens.get(i - 1).data;
								}
								type_value = type+" "+tokens.get(i).data+s_value;
							}
							save_countNodeSecondLevel = countNode;
							countNode++;
							this.dot_body+="node"+countNode+"[label="+ch+type_value+ch+"] \n";
							this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";
							i+=2;
							String s = "";
							while(!this.tokens.get(i).data.equals(";")) {
								s += tokens.get(i).data+"";
								i++;
							}
							countNode++;
							this.dot_body+="node"+countNode+"[label="+ch+s+ch+"] \n";
							this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";

						}
					}
					i++;
				 }
				}
				
			}
		
		
		return this.dot_header+this.dot_body+this.dot_footer;
	}











	public void LEFT_assignement(int i, int countNode )
	{
		String s_value ="";
		String type ="";
		String type_value ="";
		if(this.tokens.get(i).type == TokenType.TAB)
		{
			s_value = ""+tokens.get(i).data;
			i--;
			if (this.tokens.get(i).type ==TokenType.VARIABLE) {
				if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
					type = this.tokens.get(i - 1).data;
				}
				type_value = type + " " + tokens.get(i).data + s_value;
				i++;
			}
		}
		if (this.tokens.get(i).type ==TokenType.VARIABLE) {
			if (this.tokens.get(i - 1).type == TokenType.TYPEVALUES) {
				type= this.tokens.get(i - 1).data;
			}
			type_value = type+" "+tokens.get(i).data+s_value;
		}
		save_countNodeSecondLevel = countNode;
		countNode++;
		this.dot_body+="node"+countNode+"[label="+ch+type_value+ch+"] \n";
		this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";
	}

	public void RIGHT_ASSIGNEMENT(int i,int countNode)
	{
		String s = "";
		while(!this.tokens.get(i).data.equals(";")) {
			s = tokens.get(i).data+"";
			i++;
		}
		countNode++;
		this.dot_body+="node"+countNode+"[label="+ch+s+ch+"] \n";
		this.dot_body+= "node"+save_countNodeSecondLevel+" -> node"+countNode+"\n";


	}
	public void ASSIGNEMENT (int countNode,int save_countNodeKeywords,int i)
	{
		countNode++;
		this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
		this.dot_body+= "node"+save_countNodeKeywords+" -> node"+countNode+"\n";

	}
	public void CREATION_BLOCK (int i,int save_countNodeBlock)
	{

		if (tokens.get(i).data.equals("{") )
		{
			this.dot_body+="node"+countNode+"[label="+ch+"block"+ch+"] \n";
			this.dot_body+= "node"+save_countNodeBlock+" -> node"+countNode+"\n";

		}
	}
	public void CREATION_CONDITION(int i, int save_countNodeBlock)
	{
		String s2="";
		while(!tokens.get(i).data.equals(")"))
		{
			s2+= tokens.get(i+1).data+" ";
			i++;
		}
		countNode++;
		this.dot_body+="node"+countNode+"[label="+ch+s2+ch+"] \n";
		this.dot_body+= "node"+save_countNodeBlock+" -> node"+countNode+"\n";
	}
	public void CREATION_NODE_TO_LEVEL(int i)
	{
		countNode++;
		this.dot_body+="node"+countNode+"[label="+ch+tokens.get(i).data+ch+"] \n";
		this.dot_body+= "node"+save_countNodeFirstLevel+" -> node"+countNode+"\n";
	}

}
