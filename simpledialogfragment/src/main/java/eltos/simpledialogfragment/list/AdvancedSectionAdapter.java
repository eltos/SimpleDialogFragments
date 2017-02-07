package eltos.simpledialogfragment.list;

import android.support.annotation.Nullable;
import android.widget.SectionIndexer;

import java.util.ArrayList;

/**
 * An extension of AdvancedAdapter that provides a section indexer
 *
 * Created by eltos on 02.02.2017.
 */
public abstract class AdvancedSectionAdapter<T> extends AdvancedAdapter<T> implements SectionIndexer {

    class Section {
        String title;
        int startingPosition;

        Section(String text, int pos){
            title = text;
            startingPosition = pos;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private ArrayList<Section> mSections = new ArrayList<>();

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        updateAlphaIndexer();
    }

    @Override
    public int getPositionForSection(int section) {
        section = section < 0 ? 0 : section;
        section = section >= mSections.size() ? mSections.size()-1 : section;
        return mSections.get(section).startingPosition;
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = mSections.size() - 1; i >= 0; i--) {
            if (position >= mSections.get(i).startingPosition){
                return i;
            }
        }
        return 0;
    }

    @Override
    public Object[] getSections() {
        return mSections.toArray();
    }


    /**
     * Overwrite this method to return the section title corresponding with the given object.
     * Equal and consecutive titles are automatically combined to sections
     *
     * @param object an object from the underlying data set
     * @return a title representing the objects section (e.g. its first letter)
     */
    @Nullable
    public abstract String getSectionTitle(T object);


    private void updateAlphaIndexer(){
        mSections.clear();
        String currentSection = null;
        for (int i = 0; i < getCount(); i++) {
            String title = getSectionTitle(getItem(i));
            if (title != null && !title.equals(currentSection)){
                mSections.add(new Section(title, i));
                currentSection = title;
            }
        }

        if (mSections.size() == 0){
            mSections.add(new Section(null, 0));
        }

    }




}
