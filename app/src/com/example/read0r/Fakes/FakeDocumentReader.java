package com.example.read0r.Fakes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.read0r.Interfaces.IDocumentReader;

public class FakeDocumentReader implements IDocumentReader {

	private String mText = "Бавно ни залива морето от сиви тягостни дни, умът не слуша сърцето, а вечно смята и бди. Падаме в калта затъваме до шия в нея, виновни търсим за да успеем невинни в калта да заспим. Хората сега са глупави стада, водещи борба за власт и за трева, стискайки зъби подават си ръка. Не, аз не мога да спя сега! Аз сам си избрах тази съдба, вечната черна овца! вечната черна овца! Всяка глупост има си време, да стане малка правда дори, щом овчарят може да дреме, а стадото да точи зъби. Злото ви поглъща с грозната си паст, никой не посмя да чуе моя глас, браните с рогца жалката си власт. Не, аз не мога да съм като вас! Аз сам си избрах тази съдба, вечната черна овца! вечната черна овца! вечната черна овца! вечната черна овца! Сложил бях на карта сетния си час, никой не посмя да чуе моя глас, черен съм сега и в профил и в анфас. Не, аз не мога да съм като вас! Аз сам си избрах тази съдба, вечната черна овца! вечната черна овца! вечната черна овца! вечната черна овца! вечната черна овца! вечната черна овца! ";

	private int mPortionSize;
	private int mPosition;

	@Override
	public int getCurrentPosition() {
		return this.mPosition;
	}

	@Override
	public boolean endReached() {
		return this.mPosition >= this.getDocLength();
	}

	@Override
	public void setPortionSize(int portionSize) {
		this.mPortionSize = portionSize;
	}

	@Override
	public List<String> getNextWordPortion(int letterIndex) {
		ArrayList<String> results = new ArrayList<String>();
		int len = this.mText.length() - letterIndex;
		if (len > this.mPortionSize) {
			len = this.mPortionSize;
		}
		int charsCount = addWordsOfStringToCollection(
				this.mText.substring(letterIndex, letterIndex + len), results);
		this.mPosition = letterIndex + charsCount;
		return results;
	}

	private int addWordsOfStringToCollection(String textPartition,
			List<String> result) {
		int st = 0;
		for (int end = 0; end < textPartition.length(); end++) {
			if (textPartition.charAt(end) == ' ') {
				result.add(textPartition.substring(st, end));
				st = end + 1;
			}
		}
		return st;
	}

	@Override
	public long getDocLength() {
		return this.mText.length();
	}

}
