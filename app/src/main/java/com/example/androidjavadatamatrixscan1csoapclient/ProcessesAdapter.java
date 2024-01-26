package com.example.androidjavadatamatrixscan1csoapclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ProcessesAdapter extends RecyclerView.Adapter<ProcessesAdapter.ProcessesHolder> {

    private List<Process> processes;
    private Listener onProcessClickListener;

    public ProcessesAdapter(List<Process> processes, Listener onProcessClickListener) {
        this.processes = processes;
        this.onProcessClickListener = onProcessClickListener;
    }

    class ProcessesHolder extends RecyclerView.ViewHolder {

        private TextView itemNumber;
        private TextView itemDate;
        private TextView itemBlock;
        public LinearLayout holderLayout;


        public ProcessesHolder(@NonNull @NotNull View v) {
            super(v);

            itemNumber = v.findViewById(R.id.processes_item_number);
            itemDate = v.findViewById(R.id.processes_item_date);
            itemBlock = v.findViewById(R.id.processes_item_block);

            holderLayout = v.findViewById(R.id.item_processes);
        }

        public void bind(Process process) {
            itemNumber.setText(process.getNumber());
            itemDate.setText(process.getDate());
            itemBlock.setText(process.getBlock());
        }

    }

    @NonNull
    @NotNull
    @Override
    public ProcessesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.processes_item, parent, false);
        view.setOnClickListener(v -> {
            try {
                onProcessClickListener.onProcessClick((Process) v.getTag());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return new ProcessesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProcessesHolder holder, int position) {
        Process process = processes.get(position);
        holder.bind(process);
        holder.itemView.setTag(process);
    }

    @Override
    public int getItemCount() {
        return processes.size();
    }

    interface Listener {
        void onProcessClick(Process process) throws ClassNotFoundException;
    }
}
