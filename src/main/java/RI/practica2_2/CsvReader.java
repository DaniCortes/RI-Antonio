package RI.practica2_2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

public class CsvReader {

	public static Iterator<Question> readQuestions(File file) throws IOException {
		return readCsvFile(file, Question.class);
	}

	public static Iterator<Answer> readAnswers(File file) throws IOException {
		return readCsvFile(file, Answer.class);
	}

	public static Iterator<Tag> readTags(File file) throws IOException {
		return readCsvFile(file, Tag.class);
	}

	private static <T> Iterator<T> readCsvFile(File file, Class<T> type) throws IOException {

		HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();

		RFC4180Parser parser = new RFC4180ParserBuilder().build();

		strategy.setType(type);

		CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withMultilineLimit(-1)
			.withCSVParser(parser)
			.build();

		CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader).withMappingStrategy(strategy)
			.withMultilineLimit(-1)
			.build();

		return new CloseableIterator<>(csvToBean.iterator(), reader);
	}

	private static class CloseableIterator<T> implements Iterator<T>, AutoCloseable {

		private final Iterator<T> iterator;

		private final CSVReader reader;

		public CloseableIterator(Iterator<T> iterator, CSVReader reader) {
			this.iterator = iterator;
			this.reader = reader;
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = iterator.hasNext();
			if (!hasNext) {
				try {
					reader.close();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return hasNext;
		}

		@Override
		public T next() {
			return iterator.next();
		}

		@Override
		public void close() throws IOException {
			reader.close();
		}

	}

}
