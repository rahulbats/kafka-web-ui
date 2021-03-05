import { Component, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TopicsService } from '@app/_services';
import { Topic } from '@app/_models';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-create-topic',
  templateUrl: './create-topic.component.html',
  styleUrls: ['./create-topic.component.less']
})
export class CreateTopicComponent implements OnInit {
  topicForm: FormGroup;
  submitted = false;
  loading = false;
  topic: Topic;
  topicError: string;
  constructor(public activeModal: NgbActiveModal, private topicsService: TopicsService, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.topicForm = this.formBuilder.group({
      topicName: ['', Validators.required],
      partitions: ['', Validators.required],
      compacted: [false, Validators.required]
    });
    
  }

  get f() { return this.topicForm.controls; }

  onSubmit() {
        this.submitted = true;

        // stop here if form is invalid
        if (this.topicForm.invalid) {
            return;
        }

        this.loading = true;

        this.topic = new Topic(this.f.topicName.value, this.f.partitions.value,3, this.f.compacted.value);
        this.topicsService.createTopic(this.topic)
        .pipe(first())
        .subscribe(
            topics => {
                this.loading = false;
                this.activeModal.dismiss('Topic created');
                //this.onClose.emit('true');
            },
            error=> {
                this.loading = false;
                this.topicError= error;
            }
            );
  }

}
