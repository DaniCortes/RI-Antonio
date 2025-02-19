package RI.practica2_2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

import com.opencsv.exceptions.CsvValidationException;

public class CustomIndex {

	private static final String docPath = "src/main/resources/csv";

	private static final String indexPath = "src/main/resources/index";

	private static IndexWriter writer;

	private static PerFieldAnalyzerWrapper analyzer;

	CustomIndex() throws IOException, TikaException, SAXException, CsvValidationException {
		setIndexConfiguration();
	}

	public void index() throws TikaException, IOException, SAXException, CsvValidationException {
		indexFiles();
	}

	private void setIndexConfiguration() throws IOException {
		Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
		analyzerPerField.put("title", new EnglishAnalyzer(getStopWords("en")));
		analyzerPerField.put("body", new CustomAnalyzer(getStopWords("en"), false));
		analyzerPerField.put("code", new CustomAnalyzer(getStopWords("r"), true));
		analyzerPerField.put("codeString", new StandardAnalyzer());

		analyzer = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(), analyzerPerField);

		Similarity similarity = new BM25Similarity();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setSimilarity(similarity);
		iwc.setOpenMode(OpenMode.CREATE);
		FSDirectory indexDirectory = FSDirectory.open(Paths.get(indexPath));
		writer = new IndexWriter(indexDirectory, iwc);
	}

	private CharArraySet getStopWords(String language) throws IOException {
		File stopwords_file = new File("src/main/resources/stopwords/" + language);

		ArrayList<String> stopwords_list = readStopWordsFromFile(stopwords_file);

		return new CharArraySet(stopwords_list, true);
	}

	private ArrayList<String> readStopWordsFromFile(File file) throws IOException {
		ArrayList<String> stopWords = new ArrayList<>();

		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			stopWords.add(scanner.nextLine());
		}
		scanner.close();

		return stopWords;
	}

	private void indexFiles() throws TikaException, IOException, SAXException {
		File questionsFile = new File(docPath + "/Questions.csv");
		File answersFile = new File(docPath + "/Answers.csv");
		File tagsFile = new File(docPath + "/Tags.csv");

		assert questionsFile != null && answersFile != null && tagsFile != null;

		Iterator<Question> questions = CsvReader.readQuestions(questionsFile);
		Iterator<Answer> answers = CsvReader.readAnswers(answersFile);
		Iterator<Tag> tags = CsvReader.readTags(tagsFile);

		processQuestions(questions, tags);
		processAnswers(answers);
		close();
	}

	private void processQuestions(Iterator<Question> questions, Iterator<Tag> tags) throws IOException {
		int count = 0;
		Tag nextTag = null;

		if (tags.hasNext()) {
			nextTag = tags.next();
		}

		while (questions.hasNext()) {
			Question question = questions.next();

			List<String> currentTags = new ArrayList<>();

			while (nextTag != null && nextTag.getId().equals(question.getId())) {
				currentTags.add(nextTag.getTag());

				if (tags.hasNext()) {
					nextTag = tags.next();
				}
				else {
					nextTag = null;
				}
			}

			Document doc = createQuestionDoc(question, currentTags);
			writer.addDocument(doc);

			if (++count % 1000 == 0 || !questions.hasNext()) {
				writer.commit();
				System.out.println("Indexed " + count + " questions (with tags)");
			}
		}
	}

	private Document createQuestionDoc(Question question, List<String> tags) {
		Document doc = new Document();

		doc.add(new StringField("type", "question", Store.YES));

		doc.add(new StringField("id", question.getId(), Store.YES));

		doc.add(new StringField("ownerUserId", question.getOwnerUserId(), Store.YES));

		doc.add(new LongPoint("creationDate", question.getCreationDate()));

		doc.add(new IntPoint("score", question.getScore()));

		doc.add(new TextField("title", question.getTitle(), Store.YES));

		String parsedCode = getCodeInBody(question.getBody());

		doc.add(new TextField("body", question.getBody(), Store.YES));

		doc.add(new TextField("code", parsedCode, Store.NO));

		if (!tags.isEmpty()) {
			doc.add(new TextField("tags", String.join(" ", tags), Store.YES));
		}

		return doc;
	}

	private void processAnswers(Iterator<Answer> answers) throws IOException {
		int count = 0;

		while (answers.hasNext()) {
			Answer answer = answers.next();

			Document doc = createAnswerDoc(answer);
			writer.addDocument(doc);

			if (++count % 1000 == 0 || !answers.hasNext()) {
				writer.commit();
				System.out.println("Indexed " + count + " answers");
			}
		}
	}

	private Document createAnswerDoc(Answer answer) {
		Document doc = new Document();

		doc.add(new StringField("type", "answer", Store.YES));

		doc.add(new StringField("id", answer.getId(), Store.YES));

		doc.add(new StringField("ownerUserId", answer.getOwnerUserId(), Store.YES));

		doc.add(new LongPoint("creationDate", answer.getCreationDate()));

		doc.add(new StringField("parentId", answer.getParentId(), Store.YES));

		doc.add(new IntPoint("score", answer.getScore()));

		doc.add(new StringField("isAcceptedAnswer", answer.getIsAcceptedAnswer(), Store.YES));

		String parsedCode = getCodeInBody(answer.getBody());

		doc.add(new TextField("body", answer.getBody(), Store.YES));

		doc.add(new TextField("code", parsedCode, Store.YES));

		return doc;
	}

	private String getCodeInBody(String body) {
		org.jsoup.nodes.Document jsoupDoc = org.jsoup.Jsoup.parse(body);

		ArrayList<String> codeList = new ArrayList<>();

		for (Element e : jsoupDoc.getAllElements()) {

			if (e.tagName().equals("code")) {
				codeList.add(e.text());
			}

		}

		String bodyContent = String.join("\n\n\n", codeList);

		return bodyContent;
	}

	public void close() {
		try {
			writer.commit();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}

	public List<DocumentRank> searchIndex(String queryString) throws IOException, ParseException {
		List<DocumentRank> rankedDocuments = new ArrayList<>();
		FSDirectory dir = FSDirectory.open(Paths.get("src/main/resources/index"));
		DirectoryReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		QueryParser queryParser = new QueryParser("body", analyzer);
		Query query = queryParser.parse(queryString);

		TopDocs hits = searcher.search(query, 10);
		StoredFields storedFields = searcher.storedFields();

		QueryParser parentDocQueryParser = new QueryParser("id", analyzer);

		for (ScoreDoc hit : hits.scoreDocs) {
			Document doc = storedFields.document(hit.doc);
			if (doc.get("type").equals("question")) {
				String title = doc.get("title");
				String id = doc.get("id");
				float score = hit.score;
				rankedDocuments.add(new DocumentRank(title, id, score));
			}
			else {
				String parentId = doc.get("parentId");
				Query parentDocQuery = parentDocQueryParser.parse(parentId);
				TopDocs parentHits = searcher.search(parentDocQuery, 1);
				Document parentDoc = storedFields.document(parentHits.scoreDocs[0].doc);
				String title = parentDoc.get("title");
				String id = doc.get("id");
				float score = hit.score;
				rankedDocuments.add(new DocumentRank(title, id, score));
			}
		}
		return rankedDocuments;
	}

}
