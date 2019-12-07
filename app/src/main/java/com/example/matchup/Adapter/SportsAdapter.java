package com.example.matchup.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchup.Model.Sport;
import com.example.matchup.ProfileCompletionActivity;
import com.example.matchup.R;

import java.util.ArrayList;
import java.util.List;

public class SportsAdapter extends RecyclerView.Adapter<SportsAdapter.ViewHolder> {

    private List<Sport> chosenSports;
    private Context mContext;

    public SportsAdapter(Context mContext){
        this.mContext = mContext;
        this.chosenSports = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sport_item, parent, false);
        return new SportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < 1 || progress > 3) return;
                chosenSports.get(holder.getAdapterPosition()).proficiency = progress;
                holder.proficiency.setText(chosenSports.get(holder.getAdapterPosition()).getProficiencyString());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        holder.sportName.setText(chosenSports.get(holder.getAdapterPosition()).sportName);
        holder.proficiency.setText(chosenSports.get(holder.getAdapterPosition()).getProficiencyString());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileCompletionActivity)mContext).addSportBack(chosenSports.get(holder.getAdapterPosition()).sportName);

                chosenSports.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.chosenSports.size();
    }

    public void addSport(String sportName){
        Sport newSport = new Sport(sportName, 1);
        chosenSports.add(newSport);
        notifyDataSetChanged();
    }

    public List<Sport> getSports(){
        return this.chosenSports;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SeekBar seekBar;
        TextView proficiency, sportName;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sportName = itemView.findViewById(R.id.sport);
            seekBar = itemView.findViewById(R.id.seekbar);
            proficiency = itemView.findViewById(R.id.proficiency);
            deleteButton = itemView.findViewById(R.id.btndelete);
        }
    }
}
