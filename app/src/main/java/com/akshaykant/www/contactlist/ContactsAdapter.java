package com.akshaykant.www.contactlist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akshaykant.www.contactlist.entity.Contact;

import java.util.List;

/**
 * Created by Shree on 3/24/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context mContext;
    private Cursor mCursor;

    public ContactsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.activity_listitems, parent, false);

        view.setFocusable(true);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contactListItem = contactList.get(position);
        holder.tvContactName.setText(contactListItem.getDisplayName());
        holder.tvPhoneNumber.setText(contactListItem.getContactNumber().size() + "");
    }

    @Override
    public int getItemCount() {

            return 0;
    }

    /**
     * Swaps the data used by the ContactAdapter for its contact data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param contactList the new data to use as ContactAdapter's data source
     */
    void swapCursor(List<Contact> contactList) {
        this.contactList = contactList;
        //After the new Cursor is set, call notifyDataSetChanged
        notifyDataSetChanged();
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
        }
    }
}
