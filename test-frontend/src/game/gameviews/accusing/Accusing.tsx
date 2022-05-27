import './Accusing.scss';
import Players, { PlayerList, PlayerLists } from '../../players/Players';

const Hand = (props: {
	raised: boolean;
	onChange: (event: { raised: boolean }) => void;
}) => {
	return (
		<button
			className='Hand'
			onClick={() => {
				props.onChange({ raised: !props.raised });
			}}
		>
			{props.raised ? '✋raised' : '🖐️raise hand'}
		</button>
	);
};

const Done = (props: {}) => {
	return <button className='Done'>Done</button>;
};

const Discussion = (props: {}) => {
	return (
		<div className='Discussion'>
			<h1>Discussion</h1>
			<p>
				Lorem ipsum dolor sit amet consectetur adipisicing elit. Deserunt,
				asperiores aliquid minima quo, provident nostrum, sequi expedita quod
				natus repudiandae nulla eveniet obcaecati? Itaque, vel ad obcaecati nam
				in iure veniam ut illum cupiditate animi aut, recusandae hic assumenda
				velit iusto error inventore aliquid? Inventore quae ut facere repellat
				sequi!
			</p>
		</div>
	);
};

type PropsType = {
	players: PlayerList;
	send: (type: string, data: unknown) => void;
};

export const AccusingView = (props: PropsType) => {
	const me = props.players.me;
	const speaker = PlayerLists.firstWithTag(props.players, { name: 'Speaker' });
	const handButton = me.status !== 'DEAD';
	const doneButton =
		me.hasTag({ name: 'Speaker' }) ||
		(speaker && me.hasTag({ name: 'Accused', accuserID: speaker.playerID }));
	return (
		<div className='AccusingView'>
			<Players players={props.players} />
			{speaker && <Discussion />}
			{doneButton && <Done />}
			{handButton && (
				<Hand
					raised={me.hasTag({ name: 'Hand' })}
					onChange={(event) => {
						props.send('accusing/raise-hand', event.raised);
					}}
				/>
			)}
		</div>
	);
};
