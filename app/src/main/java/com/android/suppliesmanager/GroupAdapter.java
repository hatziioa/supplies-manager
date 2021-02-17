/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.suppliesmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class GroupAdapter extends ListAdapter<Group, GroupAdapter.GroupHolder> {


    private OnItemClickListener listener;


    protected GroupAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Group> DIFF_CALLBACK = new DiffUtil.ItemCallback<Group>() {
        @Override
        public boolean areItemsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.getGroupName().equals(newItem.getGroupName());
        }
    };


    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);
        return new GroupHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        Group currentGroup = getItem(position);
        holder.textViewGroupName.setText(currentGroup.getGroupName());
    }


    public Group getGroupAtPosition(int position) {
        return getItem(position);
    }


    public int getPositionOfGroup(List<Group> groups, int id) {

        for (int position = 0; position < groups.size(); position++) {
            if (groups.get(position).getId() == id) {
                return position;
            }
        }
        return -1;
    }


    class GroupHolder extends RecyclerView.ViewHolder {
        private TextView textViewGroupName;

        public GroupHolder(View itemView) {
            super(itemView);
            textViewGroupName = itemView.findViewById(R.id.text_view_group_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getBindingAdapterPosition();

                    if (listener != null && position != RecyclerView.NO_POSITION) {

                        listener.onItemClick(getItem(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getBindingAdapterPosition();

                    if (listener != null && position != RecyclerView.NO_POSITION) {

                        listener.onItemLongClick(getItem(position));
                    }
                    return false;
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(Group group);

        boolean onItemLongClick(Group group);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
