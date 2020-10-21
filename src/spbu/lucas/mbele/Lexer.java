package spbu.lucas.mbele;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.regex.Matcher;

import util.FileUtil;

public class Lexer {
	
	
	public static enum TokenType
	{
		NUMBER("-?[0-9]+"),
		VARIABLE("-?[a-zA-Z.]+"),
		BINARYOP("[*|/|+|-]"),
		PUNCTUATION(";|&&"),
		ASSIGNEMENT("[=|<|>]"),
		WHITESPACE("[\t\f\r\n]+"),
		PAREN ("[(|)]"),
		BRACKET("[{|}]"),
		TAB("\\[[^\\[]*\\]"),
		KEYWORDS("if|while|for"),
		TYPEVALUES("int|double|String|void|[|]");
	
		
		 public final String pattern;
		 private TokenType(String pattern)
		 {
			 this.pattern=pattern;
		 }
	}
	
	
	String getText() {
		return text;
	}

	public  void setText(String text) {
		this.text = text;
	}

	FileUtil fileUtil;
	private File f;
	private String path;
	private String text;
	private int position;
	private ArrayList<Token> tokens;
	
    
	//L'analyseur lexical prendra en parametre un fichier 
	public Lexer (String filePath)
	{
		
		this.f= new File(filePath);
		//L'argument text recevra les chaines de caracteres de notre fichier (Programme)
		this.path = this.f.getAbsolutePath();
		if (f.isFile()) {
			try {
				this.text = FileUtil.readFileToString(this.path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Not a File!");
		}
		this.position =0;
		
	}
	
	public ArrayList<Token> lex ()
	{
		//Liste de tokens a retourner
		 tokens = new ArrayList<Token>();
		
		//Conteneur de nos token (nom du token, le pattern du token)
		StringBuffer tokenPatternsBuffer = new StringBuffer();
		
		for (TokenType tokentype :TokenType.values())
			tokenPatternsBuffer.append(String.format("|(?<%s>%s)",tokentype.name(), tokentype.pattern));
		
		Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));
		
		Matcher matcher = tokenPatterns.matcher(text);
		while(matcher.find())
		{
			if(matcher.group(TokenType.NUMBER.name())!= null)
					{
				      tokens.add(new Token(TokenType.NUMBER,matcher.group(TokenType.NUMBER.name())));
				      continue;
					}
			else if(matcher.group(TokenType.BINARYOP.name()) !=null)
			{
				tokens.add(new Token(TokenType.BINARYOP, matcher.group(TokenType.BINARYOP.name())));
				continue;
			}
			else if(matcher.group(TokenType.ASSIGNEMENT.name()) !=null)
			{
				tokens.add(new Token(TokenType.ASSIGNEMENT, matcher.group(TokenType.ASSIGNEMENT.name())));
				continue;
			}
			else if(matcher.group(TokenType.BRACKET.name()) !=null)
			{
				tokens.add(new Token(TokenType.BRACKET, matcher.group(TokenType.BRACKET.name())));
				continue;
			}
			else if(matcher.group(TokenType.PAREN.name()) !=null)
			{
				tokens.add(new Token(TokenType.PAREN, matcher.group(TokenType.PAREN.name())));
				continue;
			}
			else if(matcher.group(TokenType.WHITESPACE.name()) !=null)
			{
				continue;
			}
			else if(matcher.group(TokenType.KEYWORDS.name()) !=null)
			{
				tokens.add(new Token(TokenType.KEYWORDS, matcher.group(TokenType.KEYWORDS.name())));
				continue;
			}
			else if(matcher.group(TokenType.VARIABLE.name()) !=null)
			{
				if (matcher.group(TokenType.VARIABLE.name()).equals("void") ||matcher.group(TokenType.VARIABLE.name()).equals("int")|| matcher.group(TokenType.VARIABLE.name()).equals("double") || matcher.group(TokenType.VARIABLE.name()).equals("String"))
					tokens.add(new Token(TokenType.TYPEVALUES, matcher.group(TokenType.VARIABLE.name())));
				else if (matcher.group(TokenType.VARIABLE.name()).equals("if") ||matcher.group(TokenType.VARIABLE.name()).equals("while")|| matcher.group(TokenType.VARIABLE.name()).equals("for"))
				    tokens.add(new Token(TokenType.KEYWORDS, matcher.group(TokenType.VARIABLE.name())));
				else
				  tokens.add(new Token(TokenType.VARIABLE, matcher.group(TokenType.VARIABLE.name())));
			
				continue;
			}
			else if(matcher.group(TokenType.TYPEVALUES.name()) !=null)
			{
				tokens.add(new Token(TokenType.TYPEVALUES, matcher.group(TokenType.TYPEVALUES.name())));
				continue;
			}
			else if(matcher.group(TokenType.TAB.name()) !=null)
			{
				tokens.add(new Token(TokenType.TAB, matcher.group(TokenType.TAB.name())));
				continue;
			}
			else if(matcher.group(TokenType.PUNCTUATION.name()) !=null)
			{
				tokens.add(new Token(TokenType.PUNCTUATION, matcher.group(TokenType.PUNCTUATION.name())));
				continue;
			}

			
		}		
	    return tokens;
	    
	    
	
	}
	
	
	public static void main(String[] args) {
		Lexer lexer = new Lexer("output/test.txt");
		ArrayList<Token> tokenList = lexer.lex();

		for (Token token : tokenList) {
			System.out.println(token);
		}
		BuildAstTree build = new BuildAstTree(lexer.lex());
		System.out.println(build.beta_try());


	}
	}
	



